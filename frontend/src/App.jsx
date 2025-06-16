import React, {useEffect, useState} from 'react';
import {Auth0Provider, useAuth0} from '@auth0/auth0-react';
import {BrowserRouter as Router, Link, Route, Routes} from 'react-router-dom';
import Login from './webpages/Login';
import Dashboard from './webpages/Dashboard';
import Profile from './webpages/Profile';
import Home from './webpages/Home';
import useProfileCompletionRedirect from "./component/userProfileRedirection";
import CompleteProfile from "./webpages/CompleteProfile";
import Personal from "./webpages/profileRedirect/Personal";
import VerificationPopup from "./component/VerificationPopup";
import Message from "./webpages/Message";

const LoggedOutNavbar = () => (
    <nav>
        <ul>
            <li><Link to="/home">Home</Link></li>
            <li><Link to="/login">Login</Link></li>
        </ul>
    </nav>
);

const LoggedInNavbar = () => {
    const {logout} = useAuth0();

    const handleLogout = () => {
        logout({logoutParams: {returnTo: window.location.origin}});
    };

    return (
        <nav>
            <ul>
                <li><Link to="/dashboard">Dashboard</Link></li>
                <li><Link to="/profile">Profile</Link></li>
                <li><Link to="/chat">Chat</Link></li>
                <li>
                    <button
                        onClick={handleLogout}
                    >
                        Logout
                    </button>
                </li>
            </ul>
        </nav>
    );
};

const LogoutPage = () => {
    const {logout} = useAuth0();
    useEffect(() => {
        logout({logoutParams: {returnTo: window.location.origin}});
    }, [logout]);
    return (
        <div>
            <h2>Logging out...</h2>
        </div>
    );
};

function AppContent() {
    const {isAuthenticated, isLoading} = useAuth0();
    const hideNavbarPaths = ['/complete-profile'];
    const [isPopupShowing, setIsPopupShowing] = useState(false);
    const shouldHideNavbar = hideNavbarPaths.includes(window.location.pathname) || isPopupShowing;

    useProfileCompletionRedirect();

    if (isLoading) {
        return <div>Loading authentication status...</div>;
    }

    return (
        <div>
            {!shouldHideNavbar && (isAuthenticated ? <LoggedInNavbar/> : <LoggedOutNavbar/>)}
            <Routes>
                <Route path="/home" element={<Home/>}/>
                <Route
                    path="/dashboard"
                    element={isAuthenticated ? <Dashboard/> : <Login/>}
                />
                <Route
                    path="/profile"
                    element={isAuthenticated ? <Profile/> : <Login/>}
                />
                <Route
                    path="/chat"
                    element={isAuthenticated ? <Message/> : <Login/>}
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
                redirect_uri: window.location.origin,
                audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                scope: `openid profile email ${CUSTOM_CLAIM_NAMESPACE}isProfileComplete`,
            }}
            cacheLocation="localstorage"
        >
            <Router>
                <AppContent/>
            </Router>
        </Auth0Provider>
    );
}