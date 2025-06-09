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
    }, [getAccessTokenSilently, isAuthenticated, isLoading, user, navigate, setAccessToken]);

    if (isLoading || apiLoading) {
        return (
            <div>
                <h1>Profile</h1>
                <p>Loading data...</p>
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
            <p>Welcome, {user.firstName || user.email}!</p>

        </div>
    );
};

export default Profile;