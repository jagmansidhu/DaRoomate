import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Auth0Provider, withAuthenticationRequired, useAuth0 } from '@auth0/auth0-react';

import Home from './Home';
import Login from './Login';
import Dashboard from './Dashboard';
// import Profile from './Profile';

const LogoutButton = () => {
    const { logout } = useAuth0();
    return (
        <button onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}>
            Log Out
        </button>
    );
};

const ProtectedDashboard = withAuthenticationRequired(Dashboard, {
    onRedirecting: () => <div>Loading dashboard...</div>,
});

// const ProtectedProfile = withAuthenticationRequired(Profile, {
//     onRedirecting: () => <div>Loading profile...</div>,
// });

export default function App() {
    return (
        <Auth0Provider
            domain={process.env.REACT_APP_AUTH0_DOMAIN}
            clientId={process.env.REACT_APP_AUTH0_CLIENT_ID}
            authorizationParams={{
                redirect_uri: window.location.origin,
                audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                scope: 'openid profile email'
            }}
        >
            <Router>
                <nav style={{ padding: '10px', borderBottom: '1px solid #ccc', display: 'flex', alignItems: 'center' }}>
                    <Link to="/home" style={{ margin: '0 10px' }}>Home</Link>
                    <Link to="/dashboard" style={{ margin: '0 10px' }}>Dashboard</Link>
                    {/*<Link to="/profile" style={{ margin: '0 10px' }}>Profile</Link>*/}
                    <div style={{ marginLeft: 'auto' }}>
                        <AuthStatus />
                    </div>
                </nav>
                <hr />
                <Routes>
                    <Route path="/home" element={<Home />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/dashboard" element={<ProtectedDashboard />} />
                    {/*<Route path="/profile" element={<ProtectedProfile />} />*/}
                    <Route path="/" element={<Home />} />
                </Routes>
            </Router>
        </Auth0Provider>
    );
}

const AuthStatus = () => {
    const { isAuthenticated, loginWithRedirect, user, isLoading } = useAuth0();

    if (isLoading) {
        return <div>Loading Auth...</div>;
    }

    return (
        <>
            {isAuthenticated ? (
                <>
                    <span style={{ marginRight: '10px' }}>Welcome, {user.name || user.email}!</span>
                    <LogoutButton />
                </>
            ) : (
                <button onClick={() => loginWithRedirect()}>Log In</button>
            )}
        </>
    );
};