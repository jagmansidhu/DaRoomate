import React, {createContext, useContext, useEffect, useState} from 'react';
import {BrowserRouter as Router, Link, Route, Routes, useNavigate} from 'react-router-dom';
import Login from './webpages/auth/Login';
import Register from './webpages/auth/Register';
import Dashboard from './webpages/Dashboard';
import Profile from './webpages/Profile';
import Home from './webpages/Home';
import useProfileCompletionRedirect from "./component/userProfileRedirection";
import CompleteProfile from "./webpages/CompleteProfile";
import Personal from "./webpages/profileRedirect/Personal";
import VerificationPopup from "./component/VerificationPopup";
import Rooms from "./webpages/room/Rooms";
import './styling/App.css';
import RoomDetailsPageWrapper from "./webpages/room/RoomDetailWrapper";

const ThemeContext = createContext();
const AuthContext = createContext();

export const useTheme = () => useContext(ThemeContext);
export const useAuth = () => useContext(AuthContext);

const ThemeProvider = ({ children }) => {
    const [isDarkMode, setIsDarkMode] = useState(() => {
        const saved = localStorage.getItem('darkMode');
        return saved ? JSON.parse(saved) : true;
    });

    useEffect(() => {
        localStorage.setItem('darkMode', JSON.stringify(isDarkMode));
        document.documentElement.setAttribute('data-theme', isDarkMode ? 'dark' : 'light');
    }, [isDarkMode]);

    return (
        <ThemeContext.Provider value={{ isDarkMode, toggleTheme: () => setIsDarkMode(prev => !prev) }}>
            {children}
        </ThemeContext.Provider>
    );
};

const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        const checkAuthStatus = async () => {
            try {
                const res = await fetch(`${process.env.REACT_APP_BASE_API_URL}/user/status`, {
                    method: 'GET',
                    credentials: 'include'
                });

                if (res.ok) {
                    setIsAuthenticated(true);
                } else {
                    setIsAuthenticated(false);
                }
            } catch (err) {
                console.error('Error checking auth status:', err);
                setIsAuthenticated(false);
            } finally {
                setIsLoading(false);
            }
        };

        checkAuthStatus();
    }, []);

    const login = () => {
        setIsAuthenticated(true);
    };

    const logout = async () => {
        try {
            await fetch(`${process.env.REACT_APP_BASE_API_URL}/user/logout`, {
                method: 'POST',
                credentials: 'include'
            });
        } catch (err) {
            console.error('Logout failed', err);
        }

        setIsAuthenticated(false);
    };

    return (
        <AuthContext.Provider value={{ isAuthenticated, isLoading, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};


const LoggedOutNavbar = () => {
    const { toggleTheme, isDarkMode } = useTheme();

    return (
        <header className="App-header">
            <div className="header-content">
                <Link to="/" className="logo">
                    <div className="logo-icon">D</div>
                    <span>DaROOmate</span>
                </Link>
                <nav className="nav">
                    <Link to="/" className="nav-link">Home</Link>
                    <Link to="/login" className="nav-link">Login</Link>
                    <button onClick={toggleTheme} className="theme-toggle">
                        {isDarkMode ? "☀️" : "🌙"}
                    </button>
                </nav>
            </div>
        </header>
    );
};

const LoggedInNavbar = () => {
    const { logout } = useAuth();
    const { toggleTheme, isDarkMode } = useTheme();

    return (
        <header className="App-header">
            <div className="header-content">
                <Link to="/dashboard" className="logo">
                    <div className="logo-icon">D</div>
                    <span>DaROOmate</span>
                </Link>
                <nav className="nav">
                    <Link to="/dashboard" className="nav-link">Dashboard</Link>
                    <Link to="/profile" className="nav-link">Profile</Link>
                    <Link to="/rooms" className="nav-link">Rooms</Link>
                    <button onClick={toggleTheme} className="theme-toggle">
                        {isDarkMode ? "☀️" : "🌙"}
                    </button>
                    <button onClick={logout} className="btn btn-secondary">Logout</button>
                </nav>
            </div>
        </header>
    );
};

const AppContent = () => {
    const { isAuthenticated, isLoading } = useAuth();
    const navigate = useNavigate();
    const hideNavbarPaths = ['/complete-profile'];
    const [isPopupShowing, setIsPopupShowing] = useState(false);
    const shouldHideNavbar = hideNavbarPaths.includes(window.location.pathname) || isPopupShowing;

    useProfileCompletionRedirect();

    useEffect(() => {
        if (isAuthenticated && !isLoading && window.location.pathname === '/') {
            navigate('/dashboard');
        }
    }, [isAuthenticated, isLoading, navigate]);

    if (isLoading) {
        return (
            <div className="loading">
                <div className="spinner"></div>
                <span>Loading authentication status...</span>
            </div>
        );
    }

    return (
        <div className="App">
            {!shouldHideNavbar && (isAuthenticated ? <LoggedInNavbar /> : <LoggedOutNavbar />)}
            <main className="main-content">
                <div className="content-wrapper">
                    <Routes>
                        <Route path="/" element={<Home />} />
                        <Route path="/home" element={<Home />} />
                        <Route path="/dashboard" element={isAuthenticated ? <Dashboard /> : <Login />} />
                        <Route path="/profile" element={isAuthenticated ? <Profile /> : <Login />} />
                        <Route path="/rooms" element={isAuthenticated ? <Rooms /> : <Login />} />
                        <Route path="/complete-profile" element={isAuthenticated ? <CompleteProfile /> : <Login />} />
                        <Route path="/update-personal" element={isAuthenticated ? <Personal /> : <Login />} />
                        <Route path="/login" element={<Login />} />
                        <Route path="/rooms/:roomId" element={<RoomDetailsPageWrapper />} />
                        <Route path="/register" element={<Register />} />

                    </Routes>
                </div>
            </main>
            <VerificationPopup onPopupVisibilityChange={setIsPopupShowing} />
        </div>
    );
};

export default function App() {
    return (
        <ThemeProvider>
            <AuthProvider>
                <Router>
                    <AppContent />
                </Router>
            </AuthProvider>
        </ThemeProvider>
    );
}