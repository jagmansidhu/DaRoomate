import React, { useEffect, useState } from 'react';
import Calendar from '../component/Calendar';
import '../styling/Dashboard.css';

const Dashboard = () => {
    const [email, setEmail] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchUserStatus = async () => {
            try {
                const res = await fetch(`${process.env.REACT_APP_BASE_API_URL}/user/status`, {
                    method: 'GET',
                    credentials: 'include',
                });

                if (!res.ok) {
                    throw new Error('Not authenticated');
                }

                const data = await res.json();
                setEmail(data.username || data.email);
            } catch (err) {
                setError(err.message);
                setEmail(null);
            } finally {
                setLoading(false);
            }
        };

        fetchUserStatus();
    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error || !email) {
        return (
            <div>
                <h1>Dashboard</h1>
                <p>Please log in to access this resource.</p>
            </div>
        );
    }

    return (
        <div className="dashboard-container">
            <div className="dashboard-header">
                <h1>Dashboard</h1>
                <p>Welcome back, {email}!</p>
            </div>
            <div className="dashboard-content">
                <Calendar />
            </div>
        </div>
    );
};

export default Dashboard;
