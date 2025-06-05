import React, {useEffect} from 'react';
import {useAuth0} from '@auth0/auth0-react';
import {BrowserRouter as Router, Routes, Route, Link} from 'react-router-dom';
import {Auth0Provider} from '@auth0/auth0-react';
import Login from './webpages/Login';
import Dashboard from './webpages/Dashboard';
import Profile from './webpages/Profile';
import Home from './webpages/Home';
import useProfileCompletionRedirect from "./component/userProfileRedirection";
import CompleteProfile from "./webpages/completeProfile";

const LoggedOutNavbar = () => (
    <nav>
        <ul>
            <li><Link to="/">Home</Link></li>
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
    const shouldHideNavbar = hideNavbarPaths.includes(window.location.pathname);

    useProfileCompletionRedirect();

    if (isLoading) {
        return <div>Loading authentication status...</div>;
    }

    return (
        <div>
            {!shouldHideNavbar && (isAuthenticated ? <LoggedInNavbar/> : <LoggedOutNavbar/>)}

            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route
                    path="/dashboard"
                    element={isAuthenticated ? <Dashboard/> : <Login/>}
                    // element={<PrivateRoute element={Dashboard}/>}
                />
                <Route
                    path="/profile"
                    element={isAuthenticated ? <Profile/> : <Login/>}

                    // element={<PrivateRoute component={Profile}/>}
                />
                <Route
                    path="/complete-profile"
                    element={isAuthenticated ? <CompleteProfile/> : <Login/>}

                    // element={<PrivateRoute component={CompleteProfile} />}
                />
                <Route path="/login" element={<Login/>}/>
                <Route path="/logout" element={<LogoutPage/>}/>
            </Routes>
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