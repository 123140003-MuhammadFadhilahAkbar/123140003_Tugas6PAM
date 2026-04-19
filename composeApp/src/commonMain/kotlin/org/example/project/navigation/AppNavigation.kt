package org.example.project.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import org.example.project.components.AppDrawerContent
import org.example.project.network.HttpClientFactory
import org.example.project.repository.NewsRepository
import org.example.project.screens.ArticleDetailScreen
import org.example.project.screens.BookmarksScreen
import org.example.project.screens.NewsListScreen
import org.example.project.screens.AddNoteScreen
import org.example.project.screens.EditNoteScreen
import org.example.project.screens.FavoritesScreen
import org.example.project.screens.NoteDetailScreen
import org.example.project.screens.NoteListScreen
import org.example.project.screens.ProfileScreenWrapper
import org.example.project.viewmodel.NewsViewModel
import org.example.project.viewmodel.NoteViewModel
import org.example.project.viewmodel.ProfileViewModel

sealed class BottomNavItem(
    val screen       : Screen,
    val icon         : ImageVector,
    val selectedIcon : ImageVector,
    val label        : String
) {
    object News      : BottomNavItem(Screen.NewsList,  Icons.Default.Home,           Icons.Default.Home,     "Berita")
    object Bookmarks : BottomNavItem(Screen.Bookmarks, Icons.Default.BookmarkBorder, Icons.Filled.Bookmark,  "Tersimpan")
    object Notes     : BottomNavItem(Screen.NoteList,  Icons.Default.List,           Icons.Default.List,     "Catatan")
    object Favorites : BottomNavItem(Screen.Favorites, Icons.Default.FavoriteBorder, Icons.Filled.Favorite,  "Favorit")
    object Profile   : BottomNavItem(Screen.Profile,   Icons.Default.Person,         Icons.Default.Person,   "Profil")
}

@Composable
fun AppNavigation() {
    val httpClient       = remember { HttpClientFactory.create() }
    val repository       = remember { NewsRepository(httpClient) }
    val newsViewModel    = remember { NewsViewModel(repository) }
    val noteViewModel    = remember { NoteViewModel() }
    val profileViewModel = remember { ProfileViewModel() }
    val profileUiState   = profileViewModel.uiState

    // Dark mode dikendalikan ProfileViewModel — berlaku global semua layar
    AnimatedContent(
        targetState    = profileUiState.isDarkMode,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label          = "theme_switch"
    ) { isDark ->
        MaterialTheme(
            colorScheme = if (isDark) buildDarkColorScheme() else buildLightColorScheme()
        ) {
            val navController = rememberNavController()
            val drawerState   = rememberDrawerState(DrawerValue.Closed)
            val scope         = rememberCoroutineScope()

            val navBackStack by navController.currentBackStackEntryAsState()
            val currentRoute  = navBackStack?.destination?.route

            val mainRoutes = listOf(
                Screen.NewsList.route,
                Screen.Bookmarks.route,
                Screen.NoteList.route,
                Screen.Favorites.route,
                Screen.Profile.route
            )

            ModalNavigationDrawer(
                drawerState     = drawerState,
                gesturesEnabled = currentRoute in mainRoutes,
                drawerContent   = {
                    // Drawer menerima isDarkMode dan onToggleDark
                    // sehingga switch di drawer berfungsi untuk semua layar
                    AppDrawerContent(
                        currentRoute  = currentRoute,
                        isDarkMode    = isDark,
                        onToggleDark  = { profileViewModel.toggleDarkMode() },
                        onNavigate    = { route ->
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        onCloseDrawer = { scope.launch { drawerState.close() } }
                    )
                }
            ) {
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    bottomBar      = { AppBottomNavBar(navController = navController) }
                ) { innerPadding ->
                    NavHost(
                        navController    = navController,
                        startDestination = Screen.NewsList.route,
                        modifier         = Modifier.padding(innerPadding)
                    ) {

                        // ── TUGAS 6: News Reader ─────────────────
                        composable(Screen.NewsList.route) {
                            NewsListScreen(
                                viewModel      = newsViewModel,
                                isDarkMode     = isDark,
                                onToggleDark   = { profileViewModel.toggleDarkMode() },
                                onArticleClick = { id ->
                                    navController.navigate(Screen.ArticleDetail.createRoute(id))
                                },
                                onMenuClick    = { scope.launch { drawerState.open() } }
                            )
                        }

                        composable(Screen.Bookmarks.route) {
                            BookmarksScreen(
                                viewModel      = newsViewModel,
                                onArticleClick = { id ->
                                    navController.navigate(Screen.ArticleDetail.createRoute(id))
                                },
                                onMenuClick    = { scope.launch { drawerState.open() } }
                            )
                        }

                        composable(
                            route     = Screen.ArticleDetail.route,
                            arguments = listOf(navArgument("articleId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
                            ArticleDetailScreen(
                                articleId = articleId,
                                viewModel = newsViewModel,
                                onBack    = { navController.popBackStack() }
                            )
                        }

                        // ── TUGAS 5: Notes App ───────────────────
                        composable(Screen.NoteList.route) {
                            NoteListScreen(
                                viewModel   = noteViewModel,
                                onNoteClick = { id -> navController.navigate(Screen.NoteDetail.createRoute(id)) },
                                onAddClick  = { navController.navigate(Screen.AddNote.route) },
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }

                        composable(Screen.Favorites.route) {
                            FavoritesScreen(
                                viewModel   = noteViewModel,
                                onNoteClick = { id -> navController.navigate(Screen.NoteDetail.createRoute(id)) },
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }

                        composable(Screen.Profile.route) {
                            ProfileScreenWrapper(
                                viewModel   = profileViewModel,
                                onMenuClick = { scope.launch { drawerState.open() } }
                            )
                        }

                        composable(
                            route     = Screen.NoteDetail.route,
                            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
                            NoteDetailScreen(
                                noteId    = noteId,
                                viewModel = noteViewModel,
                                onBack    = { navController.popBackStack() },
                                onEdit    = { id -> navController.navigate(Screen.EditNote.createRoute(id)) }
                            )
                        }

                        composable(Screen.AddNote.route) {
                            AddNoteScreen(
                                viewModel = noteViewModel,
                                onBack    = { navController.popBackStack() },
                                onSaved   = { navController.popBackStack() }
                            )
                        }

                        composable(
                            route     = Screen.EditNote.route,
                            arguments = listOf(navArgument("noteId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getInt("noteId") ?: 0
                            EditNoteScreen(
                                noteId    = noteId,
                                viewModel = noteViewModel,
                                onBack    = { navController.popBackStack() },
                                onSaved   = {
                                    navController.popBackStack(
                                        route     = Screen.NoteList.route,
                                        inclusive = false
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBottomNavBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.News,
        BottomNavItem.Bookmarks,
        BottomNavItem.Notes,
        BottomNavItem.Favorites,
        BottomNavItem.Profile
    )
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStack?.destination?.route

    if (currentRoute !in items.map { it.screen.route }) return

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick  = {
                    navController.navigate(item.screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                icon   = { Icon(if (selected) item.selectedIcon else item.icon, item.label) },
                label  = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

fun buildLightColorScheme() = lightColorScheme(
    primary          = Color(0xFF1A73E8),
    secondary        = Color(0xFF00BCD4),
    background       = Color(0xFFF8FAFF),
    surface          = Color(0xFFFFFFFF),
    surfaceVariant   = Color(0xFFF8FAFF),
    onPrimary        = Color.White,
    onBackground     = Color(0xFF1C1C2E),
    onSurface        = Color(0xFF1C1C2E),
    onSurfaceVariant = Color(0xFF6B7280),
    error            = Color(0xFFBA1A1A),
    errorContainer   = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    outline          = Color(0xFFE0E4F0),
    primaryContainer = Color(0xFFD3E4FF),
    secondaryContainer   = Color(0xFFCCFBF1),
    onSecondaryContainer = Color(0xFF134E4A)
)

fun buildDarkColorScheme() = darkColorScheme(
    primary          = Color(0xFF7AB3F5),
    secondary        = Color(0xFF00BCD4),
    background       = Color(0xFF0F1117),
    surface          = Color(0xFF1A1D26),
    surfaceVariant   = Color(0xFF252836),
    onPrimary        = Color(0xFF003060),
    onBackground     = Color(0xFFE8EAF0),
    onSurface        = Color(0xFFE8EAF0),
    onSurfaceVariant = Color(0xFF9EA3B0),
    error            = Color(0xFFFFB4AB),
    errorContainer   = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    outline          = Color(0xFF2E3347),
    primaryContainer = Color(0xFF004496),
    secondaryContainer   = Color(0xFF134E4A),
    onSecondaryContainer = Color(0xFFCCFBF1)
)
