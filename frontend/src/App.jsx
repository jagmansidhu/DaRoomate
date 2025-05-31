import React from 'react';
import {useAuth0} from '@auth0/auth0-react';
import {BrowserRouter as Router, Routes, Route, Link} from 'react-router-dom';
import {Auth0Provider} from '@auth0/auth0-react';
import Login from './Login';
import Dashboard from './Dashboard';
import Profile from './Profile';
import Home from './Home';

const LoggedOutNavbar = () => (
    <nav style={{backgroundColor: '#f0f0f0', padding: '10px'}}>
        <ul style={{listStyleType: 'none', margin: 0, padding: 0, display: 'flex', gap: '20px'}}>
            <li><Link to="/">Home</Link></li>
            <li><Link to="/login">Login</Link></li>
        </ul>
    </nav>
);

const LoggedInNavbar = () => (
    <nav style={{backgroundColor: '#e0e0e0', padding: '10px'}}>
        <ul style={{listStyleType: 'none', margin: 0, padding: 0, display: 'flex', gap: '20px'}}>
            <li><Link to="/dashboard">Dashboard</Link></li>
            <li><Link to="/profile">Profile</Link></li>
            <li><Link to="/logout">Logout</Link></li>
        </ul>
    </nav>
);

function AppContent() {
    const {isAuthenticated, isLoading} = useAuth0();

    if (isLoading) {
        return <div>Loading authentication status...</div>;
    }

    return (
        <div>
            {isAuthenticated ? <LoggedInNavbar/> : <LoggedOutNavbar/>}

            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route
                    path="/dashboard"
                    element={isAuthenticated ? <Dashboard/> : <Login/>}
                />
                <Route
                    path="/profile"
                    element={isAuthenticated ? <Profile/> : <Login/>}
                />
                <Route path="/login" element={<Login/>}/>
                <Route path="/logout" element={<LogoutPage/>}/>
            </Routes>
        </div>
    );
}


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
            cacheLocation="localstorage"
        >
            <Router>
                <AppContent/>
            </Router>
        </Auth0Provider>
    );
}


const LogoutPage = () => {
    const {logout} = useAuth0();
    React.useEffect(() => {
        logout({logoutParams: {returnTo: window.location.origin}});
    }, [logout]);
    return <h2>Logging out...</h2>;
};