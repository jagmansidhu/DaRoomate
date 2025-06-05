import React, {useEffect, useState} from 'react';
import {useAuth0} from '@auth0/auth0-react';
import axios from 'axios';

const Dashboard = () => {
    const {getAccessTokenSilently, user, isLoading, isAuthenticated} = useAuth0();
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [apiLoading, setApiLoading] = useState(true);
    const [accessToken, setAccessToken] = useState(null);


    useEffect(() => {
        const fetchProtectedData = async () => {
            if (!isAuthenticated || isLoading) {
                setApiLoading(false);
                return;
            }

            try {
                const accessToken = await getAccessTokenSilently({
                    authorizationParams: {
                        audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                        scope: 'read:data',
                    },
                });
                setAccessToken(accessToken);

                const response = await axios.get('http://localhost:8085/api/create_or_find_user', {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                });
                setData(response.data);
            } catch (err) {
                console.error('Error fetching protected resource:', err);
                setError(err);
            } finally {
                setApiLoading(false);
            }
        };

        fetchProtectedData();
    })

    if (isLoading || apiLoading) {
        return (
            <div>
                <h1>Dashboard</h1>
                <p>Loading data...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div>
                <h1>Dashboard</h1>
                <p style={{color: 'red'}}>Error: {error.message}. Please try logging in again.</p>
                {accessToken && (
                    <>
                        <h3>Access Token (for debugging):</h3>
                        <textarea value={accessToken} readOnly rows="5" cols="80" style={{wordWrap: 'break-word'}}/>
                    </>
                )}
            </div>

        );
    }

    if (!isAuthenticated) {
        return (
            <div>
                <h1>Dashboard</h1>
                <p>You need to be logged in to view the dashboard.</p>
            </div>
        );
    }

    return (
        <div>
            <h1>Dashboard Data</h1>
            <p>Welcome, {user.name || user.email}!</p>
            <pre>{JSON.stringify(data, null, 2)}</pre>
        </div>
    );
};

export default Dashboard;