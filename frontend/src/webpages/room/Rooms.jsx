import React, { useState, useEffect } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';
import '../../styling/Rooms.css';

import CreateRoom from '../room/CreateRoom';
import JoinRoom from '../room/JoinRoom';
import RoomDetails from '../room/RoomDetails';
import RoleManagement from '../room/RoleManage';

const Rooms = () => {
    const { getAccessTokenSilently, user } = useAuth0();
    const [rooms, setRooms] = useState([]);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showJoinModal, setShowJoinModal] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    const [selectedRoom, setSelectedRoom] = useState(null);
    const [showRoomDetails, setShowRoomDetails] = useState(false);
    const [showRoleManagement, setShowRoleManagement] = useState(false);

    useEffect(() => {
        fetchRooms();
    }, []);

    const fetchRooms = async () => {
        try {
            setLoading(true);
            const accessToken = await getAccessTokenSilently();

            const response = await axios.get(`${process.env.REACT_APP_BASE_API_URL}/api/rooms`, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            });

            setRooms(response.data);
            setError(null);
        } catch (error) {
            console.error('Error fetching rooms:', error);
            setError('Failed to load rooms');
        } finally {
            setLoading(false);
        }
    };

    const handleCreateRoom = (newRoomData) => {
        setRooms([...rooms, newRoomData]);
    };

    const handleRoomJoined = () => {
        fetchRooms();
    };

    const openRoomDetails = (room) => {
        setSelectedRoom(room);
        setShowRoomDetails(true);
    };

    const isHeadRoommate = (room) => {
        return room.headRoommateId === user?.sub;
    };

    const deleteRoom = async () => {
        if (!selectedRoom) return;
        if (!window.confirm('Are you sure you want to delete this room? This cannot be undone.')) return;
        try {
            const accessToken = await getAccessTokenSilently();
            await axios.delete(`${process.env.REACT_APP_BASE_API_URL}/api/rooms/${selectedRoom.id}/removeroom`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });
            setShowRoomDetails(false);
            setRooms(rooms.filter(r => r.id !== selectedRoom.id));
            setError(null);
        } catch (error) {
            console.error('Error deleting room:', error);
            setError('Failed to delete room.');
        }
    };

    const removeMember = async (memberId) => {
        if (!selectedRoom || !memberId) return;
        if (!window.confirm('Are you sure you want to remove this member from the room?')) return;

        try {
            const accessToken = await getAccessTokenSilently();
            await axios.delete(`${process.env.REACT_APP_BASE_API_URL}/api/rooms/${selectedRoom.id}/members/${memberId}`, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            });

            fetchRooms();
            setSelectedRoom(prev => ({
                ...prev,
                members: prev.members.filter(m => m.id !== memberId),
            }));
            setError(null);
        } catch (error) {
            console.error('Error removing member:', error);
            setError('Failed to remove member.');
        }
    };

    const leaveRoom = async () => {
        if (!selectedRoom) return;
        if (!window.confirm('Are you sure you want to leave this room?')) return;
        try {
            const accessToken = await getAccessTokenSilently();
            await axios.delete(`${process.env.REACT_APP_BASE_API_URL}/api/rooms/${selectedRoom.id}/leave`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });
            setShowRoomDetails(false);
            setRooms(rooms.filter(r => r.id !== selectedRoom.id));
            setError(null);
        } catch (error) {
            console.error('Error leaving room:', error);
            setError('Failed to leave room.');
        }
    };

    const handleManageRolesClick = () => {
        setShowRoomDetails(false);
        setShowRoleManagement(true);
    };

    if (loading) {
        return (
            <div className="rooms-container">
                <div className="loading">
                    <div className="spinner"></div>
                    <span>Loading rooms...</span>
                </div>
            </div>
        );
    }

    return (
        <div className="rooms-container">
            <div className="rooms-header">
                <h1>My Rooms</h1>
                <p>Manage your shared living spaces</p>
                <div className="rooms-actions">
                    <button
                        className="btn btn-primary"
                        onClick={() => setShowCreateModal(true)}
                    >
                        Create New Room
                    </button>
                    <button
                        className="btn btn-secondary"
                        onClick={() => setShowJoinModal(true)}
                    >
                        Join Room
                    </button>
                </div>
            </div>

            {error && (
                <div className="alert alert-error">
                    {error}
                    <button onClick={() => setError(null)}>×</button>
                </div>
            )}

            <div className="rooms-grid">
                {rooms.length === 0 ? (
                    <div className="empty-state">
                    </div>
                ) : (
                    rooms.map((room) => (
                        <div key={room.id} className="room-card">
                            <div className="room-header">
                                <h3>{room.name}</h3>
                                {isHeadRoommate(room) && (
                                    <span className="role-badge HEAD_ROOMMATE">Head Roommate</span>
                                )}
                            </div>
                            <p className="room-address">{room.address}</p>
                            <p className="room-description">{room.description}</p>
                            <div className="room-members">
                                <span className="member-count">
                                    {room.members?.length || 0} members
                                </span>
                            </div>
                            <div className="room-actions">
                                <button
                                    className="btn btn-primary"
                                    onClick={() => openRoomDetails(room)}
                                >
                                    View Details
                                </button>
                                {isHeadRoommate(room) && (
                                    <button
                                        className="btn btn-secondary"
                                        onClick={() => {
                                            setSelectedRoom(room);
                                            setShowRoleManagement(true);
                                        }}
                                    >
                                        Manage Roles
                                    </button>
                                )}
                            </div>
                        </div>
                    ))
                )}
            </div>

            <CreateRoom
                show={showCreateModal}
                onClose={() => setShowCreateModal(false)}
                onCreateRoom={handleCreateRoom}
                getAccessTokenSilently={getAccessTokenSilently}
            />

            <JoinRoom
                show={showJoinModal}
                onClose={() => setShowJoinModal(false)}
                onRoomJoined={handleRoomJoined}
                getAccessTokenSilently={getAccessTokenSilently}
            />

            <RoomDetails
                show={showRoomDetails}
                onClose={() => setShowRoomDetails(false)}
                room={selectedRoom}
                onLeaveRoom={leaveRoom}
                onDeleteRoom={deleteRoom}
                onRemoveMember={removeMember}
                onManageRolesClick={handleManageRolesClick}
            />

            <RoleManagement
                show={showRoleManagement}
                room={selectedRoom}
                onClose={() => setShowRoleManagement(false)}
                onUpdate={fetchRooms}
                getAccessTokenSilently={getAccessTokenSilently}
            />
        </div>
    );
};

export default Rooms;