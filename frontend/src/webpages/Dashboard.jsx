import React, { useEffect, useState } from 'react';
import '../styling/Dashboard.css';

const Dashboard = () => {
    const [email, setEmail] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [chores, setChores] = useState([]);
    const [utilities, setUtilities] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const userRes = await fetch(`${process.env.REACT_APP_BASE_API_URL}/user/status`, {
                    method: 'GET',
                    withCredentials: true,
                    credentials: 'include',
                });
                if (!userRes.ok) {
                    throw new Error('Not authenticated');
                }
                const userData = await userRes.json();
                const userEmail = userData.username || userData.email;
                setEmail(userEmail);

                const [choresRes, utilitiesRes] = await Promise.all([
                    fetch(`${process.env.REACT_APP_BASE_API_URL}/api/chores/upcoming?id=${userEmail}`, {
                        method: 'GET',
                        withCredentials: true,
                        credentials: 'include',
                    }),
                    fetch(`${process.env.REACT_APP_BASE_API_URL}/api/utility/upcoming?id=${userEmail}`, {
                        method: 'GET',
                        withCredentials: true,
                        credentials: 'include',
                    }),
                ]);

                if (choresRes.ok) {
                    const choresData = await choresRes.json();
                    setChores(choresData);
                }

                if (utilitiesRes.ok) {
                    const utilitiesData = await utilitiesRes.json();
                    setUtilities(utilitiesData);
                }
            } catch (err) {
                setError(err.message);
                setEmail(null);
                console.error('Error fetching data:', err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [email]);

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

    const upcomingChores = chores
        .filter(chore => new Date(chore.dueAt) > new Date())
        .sort((a, b) => new Date(a.dueAt) - new Date(b.dueAt));
    const upcomingUtilities = utilities;

    return (
        <div className="dashboard-container">
            <div className="dashboard-header">
                <h1>Dashboard</h1>
                <p>Welcome back, {email}!</p>
            </div>
            <div className="dashboard-content">
                <h2>Upcoming Chores</h2>
                {upcomingChores.length > 0 ? (
                    <ul>
                        {upcomingChores.map(chore => (
                            <li key={chore.id}>
                                {chore.choreName}
                                - Due: {new Date(chore.dueAt).toLocaleDateString()}
                                - Room: {chore.roomName}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No upcoming chores.</p>
                )}

                <h2>Upcoming Utilities</h2>
                {upcomingUtilities.length > 0 ? (
                    <ul>
                        {upcomingUtilities.map(utility => (
                            <li key={utility.id}>
                                {utility.utilityName}
                                - Price: ${utility.utilityPrice}
                                - Room: {utility.roomName}
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No upcoming utilities.</p>
                )}
            </div>
        </div>
    );
};

export default Dashboard;