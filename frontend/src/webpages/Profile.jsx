import React, { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Profile = () => {
    const { getAccessTokenSilently, user, isLoading, isAuthenticated } = useAuth0();
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);
    const [apiLoading, setApiLoading] = useState(true);
    const [accessToken, setAccessToken] = useState(null);

    const navigate = useNavigate();


    useEffect(() => {
        const checkProfileAndFetchData = async () => {
            if (isLoading) {
                setApiLoading(true);
                return;
            }

            if (!isAuthenticated) {
                setApiLoading(false);
                return;
            }
            try {
                const fetchedAccessToken = await getAccessTokenSilently({
                    authorizationParams: {
                        audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                        scope: 'read:data',
                    },
                });
                setAccessToken(fetchedAccessToken);

                const response = await axios.get('http://localhost:8085/api/create_or_find_user', {
                    headers: {
                        Authorization: `Bearer ${fetchedAccessToken}`,
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

        checkProfileAndFetchData();
    }, [getAccessTokenSilently, isAuthenticated, isLoading, user, navigate]);

    if (isLoading || apiLoading) {
        return (
            <div>
                <h1>Profile</h1>
                <p>Loading data...</p>
            </div>
        );
    }

    if (error) {
        return (
            <div>
                <h1>Profile</h1>
                <p style={{ color: 'red' }}>Error: {error.message}. Please try logging in again.</p>
                {accessToken && (
                    <>
                        <h3>Access Token (for debugging):</h3>
                        <textarea value={accessToken} readOnly rows="5" cols="80" style={{ wordWrap: 'break-word' }} />
                    </>
                )}
            </div>
        );
    }

    if (!isAuthenticated) {
        return (
            <div>
                <h1>Profile</h1>
                <p>You need to be logged in to view the Profile.</p>
            </div>
        );
    }

    return (
        <div className="profile">
            <h1>Profile Data</h1>
            <p>Welcome, {user.name || user.email}!</p>

            <h3>Protected API Data:</h3>
            <pre>{JSON.stringify(data, null, 2)}</pre>

            <h3>Auth0 User Object (for debugging):</h3>
            <pre>{JSON.stringify(user, null, 2)}</pre>
        </div>
    );
};

export default Profile;