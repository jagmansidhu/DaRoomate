import React, {useEffect, useState} from 'react';
import {useAuth0} from '@auth0/auth0-react';
import axios from 'axios';
import Calendar from '../component/Calendar';
import '../styling/Dashboard.css';

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

            async function getToken() {
                try {
                            const accessToken = await getAccessTokenSilently();
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
            }

            await getToken();
        };

        fetchProtectedData();
    },[getAccessTokenSilently, isAuthenticated, isLoading, user, setAccessToken]);

    if (isLoading || apiLoading) {
        return (
            <div>
                <h1>Dashboard</h1>
                <p>Loading data...</p>
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
        <div className="dashboard-container">
            <div className="dashboard-header">
                <h1>Dashboard</h1>
                <p>Welcome back, {user.name || user.email}!</p>
            </div>
            
            <div className="dashboard-content">
                <Calendar />
            </div>
        </div>
    );
};

export default Dashboard;