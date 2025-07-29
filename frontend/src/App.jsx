import React, {useEffect, useState} from 'react';
import {Auth0Provider, useAuth0} from '@auth0/auth0-react';
import {BrowserRouter as Router, Link, Route, Routes, useNavigate} from 'react-router-dom';
import Login from './webpages/Login';
import Dashboard from './webpages/Dashboard';
import Profile from './webpages/Profile';
import Home from './webpages/Home';
import useProfileCompletionRedirect from "./component/userProfileRedirection";
import CompleteProfile from "./webpages/CompleteProfile";
import Personal from "./webpages/profileRedirect/Personal";
import VerificationPopup from "./component/VerificationPopup";
// import Message from "./webpages/Message";
// import Friends from "./webpages/Friends";
import Rooms from "./webpages/room/Rooms";
import './styling/App.css';

const ThemeContext = React.createContext();

export const useTheme = () => {
  const context = React.useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};

const ThemeProvider = ({ children }) => {
  const [isDarkMode, setIsDarkMode] = useState(() => {
    const saved = localStorage.getItem('darkMode');
    return saved ? JSON.parse(saved) : true;
  });

  useEffect(() => {
    localStorage.setItem('darkMode', JSON.stringify(isDarkMode));
    document.documentElement.setAttribute('data-theme', isDarkMode ? 'dark' : 'light');
  }, [isDarkMode]);

  const toggleTheme = () => {
    setIsDarkMode(!isDarkMode);
  };

  return (
    <ThemeContext.Provider value={{ isDarkMode, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
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
          <button
            onClick={toggleTheme}
            className="theme-toggle"
            title={isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode"}
          >
            {isDarkMode ? "☀️" : "🌙"}
          </button>
        </nav>
      </div>
    </header>
  );
};

const LoggedInNavbar = () => {
  const {logout} = useAuth0();
  const { toggleTheme, isDarkMode } = useTheme();

  const handleLogout = () => {
    logout({logoutParams: {returnTo: window.location.origin}});
  };

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
          {/*<Link to="/chat" className="nav-link">Chat</Link>*/}
          {/*<Link to="/friends" className="nav-link">Friends</Link>*/}
          <button
            onClick={toggleTheme}
            className="theme-toggle"
            title={isDarkMode ? "Switch to Light Mode" : "Switch to Dark Mode"}
          >
            {isDarkMode ? "☀️" : "🌙"}
          </button>
          <button
            onClick={handleLogout}
            className="btn btn-secondary"
          >
            Logout
          </button>
        </nav>
      </div>
    </header>
  );
};

const LogoutPage = () => {
  const {logout} = useAuth0();
  useEffect(() => {
    logout({logoutParams: {returnTo: window.location.origin}});
  }, [logout]);
  return (
    <div className="loading">
      <div className="spinner"></div>
      <span>Logging out...</span>
    </div>
  );
};

function AppContent() {
  const {isAuthenticated, isLoading} = useAuth0();
  const navigate = useNavigate();
  const hideNavbarPaths = ['/complete-profile'];
  const [isPopupShowing, setIsPopupShowing] = useState(false);
  const shouldHideNavbar = hideNavbarPaths.includes(window.location.pathname) || isPopupShowing;

  useProfileCompletionRedirect();

  // Redirect authenticated users from home page to dashboard
  useEffect(() => {
    if (isAuthenticated && !isLoading && window.location.pathname === '/') {
      // Check if user is on the home page and redirect to dashboard
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
      {!shouldHideNavbar && (isAuthenticated ? <LoggedInNavbar/> : <LoggedOutNavbar/>)}
      <main className="main-content">
        <div className="content-wrapper">
          <Routes>
            <Route path="/" element={<Home/>}/>
            <Route path="/home" element={<Home/>}/>
            <Route
              path="/dashboard"
              element={isAuthenticated ? <Dashboard/> : <Login/>}
            />
            <Route
              path="/profile"
              element={isAuthenticated ? <Profile/> : <Login/>}
            />
            {/*<Route*/}
            {/*  path="/chat"*/}
            {/*  element={isAuthenticated ? <Message/> : <Login/>}*/}
            {/*/>*/}
            {/*<Route*/}
            {/*  path="/friends"*/}
            {/*  element={isAuthenticated ? <Friends/> : <Login/>}*/}
            {/*/>*/}
            <Route
              path="/rooms"
              element={isAuthenticated ? <Rooms/> : <Login/>}
            />
            <Route
              path="/complete-profile"
              element={isAuthenticated ? <CompleteProfile/> : <Login/>}
            />
            <Route
              path="/update-personal"
              element={isAuthenticated ? <Personal/> : <Login/>}
            />
            <Route path="/login" element={<Login/>}/>
            <Route path="/logout" element={<LogoutPage/>}/>
          </Routes>
        </div>
      </main>

      <VerificationPopup onPopupVisibilityChange={setIsPopupShowing} />
    </div>
  );
}

export default function App() {

  const CUSTOM_CLAIM_NAMESPACE = 'https://daroomate.org/';

  return (
    <Auth0Provider
      domain={process.env.REACT_APP_AUTH0_DOMAIN}
      clientId={process.env.REACT_APP_AUTH0_CLIENT_ID}
      authorizationParams={{
        redirect_uri: `${window.location.origin}/dashboard`,
        audience: process.env.REACT_APP_AUTH0_AUDIENCE,
        scope: `openid profile email ${CUSTOM_CLAIM_NAMESPACE}isProfileComplete read:data write:data`,
      }}
      cacheLocation="localstorage"
    >
      <ThemeProvider>
        <Router>
          <AppContent/>
        </Router>
      </ThemeProvider>
    </Auth0Provider>
  );
}