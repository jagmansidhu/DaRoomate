import React, { useState, useEffect } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';
import '../styling/Rooms.css';

const Rooms = () => {
    const { getAccessTokenSilently, user } = useAuth0();
    const [rooms, setRooms] = useState([]);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [showJoinModal, setShowJoinModal] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    
    const [newRoom, setNewRoom] = useState({
        name: '',
        address: '',
        description: ''
    });
    const [joinRoomCode, setJoinRoomCode] = useState('');
    
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

            const response = await axios.get('http://localhost:8085/api/rooms', {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            });

            setRooms(response.data);
        } catch (error) {
            console.error('Error fetching rooms:', error);
            setError('Failed to load rooms');
        } finally {
            setLoading(false);
        }
    };

    const createRoom = async (e) => {
        e.preventDefault();
        try {
            const accessToken = await getAccessTokenSilently({
                authorizationParams: {
                    audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                    scope: 'write:data',
                },
            });

            const response = await axios.post('http://localhost:8085/api/rooms', newRoom, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            });

            setRooms([...rooms, response.data]);
            setShowCreateModal(false);
            setNewRoom({ name: '', address: '', description: '' });
        } catch (error) {
            console.error('Error creating room:', error);
            setError('Failed to create room');
        }
    };

    const joinRoom = async (e) => {
        e.preventDefault();
        try {
            const accessToken = await getAccessTokenSilently({
                authorizationParams: {
                    audience: process.env.REACT_APP_AUTH0_AUDIENCE,
                    scope: 'write:data',
                },
            });

            await axios.post(`http://localhost:8085/api/rooms/${joinRoomCode}/join`, {}, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
            });

            setShowJoinModal(false);
            setJoinRoomCode('');
            fetchRooms(); // Refresh rooms list
        } catch (error) {
            console.error('Error joining room:', error);
            setError('Failed to join room. Please check the room code.');
        }
    };

    const openRoomDetails = (room) => {
        setSelectedRoom(room);
        setShowRoomDetails(true);
    };

    const isHeadRoommate = (room) => {
        return room.headRoommateId === user?.sub;
    };

    // Add deleteRoom function
    const deleteRoom = async () => {
        if (!selectedRoom) return;
        if (!window.confirm('Are you sure you want to delete this room? This cannot be undone.')) return;
        try {
            const accessToken = await getAccessTokenSilently();
            await axios.delete(`http://localhost:8085/api/rooms/${selectedRoom.id}/removeroom`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });
            setShowRoomDetails(false);
            setRooms(rooms.filter(r => r.id !== selectedRoom.id));
        } catch (error) {
            console.error('Error deleting room:', error);
            setError('Failed to delete room.');
        }
    };

    // Add leaveRoom function
    const leaveRoom = async (memberId) => {
        if (!selectedRoom) return;
        if (!window.confirm('Are you sure you want to leave this room?')) return;
        try {
            const accessToken = await getAccessTokenSilently();
            await axios.delete(`http://localhost:8085/api/rooms/${selectedRoom.id}/members/${memberId}`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });
            setShowRoomDetails(false);
            setRooms(rooms.filter(r => r.id !== selectedRoom.id));
        } catch (error) {
            console.error('Error leaving room:', error);
            setError('Failed to leave room.');
        }
    };

    // Add leaveRoomFromCard function
    const leaveRoomFromCard = async (room) => {
        if (!room || !user) return;
        // Find the current user's memberId in this room
        const member = room.members?.find(m => m.userId === user.sub);
        if (!member) return;
        if (!window.confirm('Are you sure you want to leave this room?')) return;
        try {
            const accessToken = await getAccessTokenSilently();
            await axios.delete(`http://localhost:8085/api/rooms/${room.id}/members/${member.id}`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });
            setRooms(rooms.filter(r => r.id !== room.id));
        } catch (error) {
            console.error('Error leaving room:', error);
            setError('Failed to leave room.');
        }
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
                        <div className="empty-icon">🏠</div>
                        <h3>No rooms yet</h3>
                        <p>Create a new room or join an existing one to get started</p>
                        <div className="empty-actions">
                            <button 
                                className="btn btn-primary"
                                onClick={() => setShowCreateModal(true)}
                            >
                                Create Your First Room
                            </button>
                        </div>
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
                                {/* Leave Room button for non-head roommates removed */}
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* Create Room Modal */}
            {showCreateModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <div className="modal-header">
                            <h2>Create New Room</h2>
                            <button 
                                className="modal-close"
                                onClick={() => setShowCreateModal(false)}
                            >
                                ×
                            </button>
                        </div>
                        <form onSubmit={createRoom}>
                            <div className="form-group">
                                <label htmlFor="roomName">Room Name</label>
                                <input
                                    type="text"
                                    id="roomName"
                                    value={newRoom.name}
                                    onChange={(e) => setNewRoom({...newRoom, name: e.target.value})}
                                    placeholder="Enter room name"
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="roomAddress">Address</label>
                                <input
                                    type="text"
                                    id="roomAddress"
                                    value={newRoom.address}
                                    onChange={(e) => setNewRoom({...newRoom, address: e.target.value})}
                                    placeholder="Enter address"
                                    required
                                />
                            </div>
                            <div className="form-group">
                                <label htmlFor="roomDescription">Description</label>
                                <textarea
                                    id="roomDescription"
                                    value={newRoom.description}
                                    onChange={(e) => setNewRoom({...newRoom, description: e.target.value})}
                                    placeholder="Enter description"
                                    rows="3"
                                />
                            </div>
                            <div className="modal-actions">
                                <button type="button" className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    Create Room
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Join Room Modal */}
            {showJoinModal && (
                <div className="modal-overlay">
                    <div className="modal">
                        <div className="modal-header">
                            <h2>Join Room</h2>
                            <button 
                                className="modal-close"
                                onClick={() => setShowJoinModal(false)}
                            >
                                ×
                            </button>
                        </div>
                        <form onSubmit={joinRoom}>
                            <div className="form-group">
                                <label htmlFor="roomCode">Room Code</label>
                                <input
                                    type="text"
                                    id="roomCode"
                                    value={joinRoomCode}
                                    onChange={(e) => setJoinRoomCode(e.target.value)}
                                    placeholder="Enter room code"
                                    required
                                />
                                <small>Ask the head roommate for the room code</small>
                            </div>
                            <div className="modal-actions">
                                <button type="button" className="btn btn-secondary" onClick={() => setShowJoinModal(false)}>
                                    Cancel
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    Join Room
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* Room Details Modal (add Leave Room and Delete Room buttons) */}
            {showRoomDetails && selectedRoom && (
                <div className="modal-overlay">
                    <div className="modal modal-large">
                        <div className="modal-header">
                            <h2>{selectedRoom.name}</h2>
                            <button 
                                className="modal-close"
                                onClick={() => setShowRoomDetails(false)}
                            >
                                ×
                            </button>
                        </div>
                        <div className="room-details">
                            <div className="detail-section">
                                <h3>Room Information</h3>
                                <p><strong>Address:</strong> {selectedRoom.address}</p>
                                <p><strong>Description:</strong> {selectedRoom.description}</p>
                                <p><strong>Room Code:</strong> <code>{selectedRoom.roomCode}</code></p>
                            </div>
                            <div className="detail-section">
                                <h3>Members</h3>
                                <div className="members-list">
                                    {selectedRoom.members?.map((member) => (
                                        <div key={member.id} className="member-item">
                                            <div className="member-info">
                                                <span className="member-name">{member.name}</span>
                                                <span className="member-role">{member.role}</span>
                                            </div>
                                            {/* Leave Room button for current user (not head roommate) */}
                                            {member.userId === user?.sub && member.role !== 'HEAD_ROOMMATE' && (
                                                <button
                                                    className="btn btn-danger"
                                                    onClick={() => leaveRoom(member.id)}
                                                >
                                                    Leave Room
                                                </button>
                                            )}
                                        </div>
                                    ))}
                                </div>
                            </div>
                            {isHeadRoommate(selectedRoom) && (
                                <div className="detail-section">
                                    <h3>Management</h3>
                                    <div className="management-actions">
                                        <button 
                                            className="btn btn-primary"
                                            onClick={() => {
                                                setShowRoomDetails(false);
                                                setShowRoleManagement(true);
                                            }}
                                        >
                                            Manage Roles
                                        </button>
                                        {/* Add Calendar button removed */}
                                        <button
                                            className="btn btn-danger"
                                            onClick={deleteRoom}
                                        >
                                            Delete Room
                                        </button>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            )}

            {showRoleManagement && selectedRoom && (
                <RoleManagementModal 
                    room={selectedRoom}
                    onClose={() => setShowRoleManagement(false)}
                    onUpdate={fetchRooms}
                    getAccessTokenSilently={getAccessTokenSilently}
                />
            )}
        </div>
    );
};

const RoleManagementModal = ({ room, onClose, onUpdate, getAccessTokenSilently }) => {
    const [members, setMembers] = useState(room.members || []);
    const [loading, setLoading] = useState(false);

    const updateMemberRole = async (memberId, newRole) => {
        try {
            setLoading(true);
            const accessToken = await getAccessTokenSilently();
            await axios.put(`http://localhost:8085/api/rooms/${room.id}/members/${memberId}/role`, 
                { role: newRole },
                {
                    headers: {
                        Authorization: `Bearer ${accessToken}`,
                    },
                }
            );
            setMembers(members.map(member => 
                member.id === memberId ? { ...member, role: newRole } : member
            ));
            onUpdate();
        } catch (error) {
            console.error('Error updating member role:', error);
        } finally {
            setLoading(false);
        }
    };

    // Add removeMember function
    const removeMember = async (memberId) => {
        if (!window.confirm('Are you sure you want to remove this member from the room?')) return;
        try {
            setLoading(true);
            const accessToken = await getAccessTokenSilently();
            await axios.delete(`http://localhost:8085/api/rooms/${room.id}/members/${memberId}`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });
            setMembers(members.filter((m) => m.id !== memberId));
            onUpdate();
        } catch (error) {
            console.error('Error removing member:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal modal-large">
                <div className="modal-header">
                    <h2>Manage Roles - {room.name}</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>
                <div className="role-management">
                    <div className="members-table">
                        <table>
                            <thead>
                                <tr>
                                    <th>Member</th>
                                    <th>Current Role</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {members.map((member) => (
                                    <tr key={member.id}>
                                        <td>{member.name}</td>
                                        <td>
                                            <span className={`role-badge ${member.role}`}>
                                                {member.role}
                                            </span>
                                        </td>
                                        <td>
                                            {member.role !== 'HEAD_ROOMMATE' && (
                                                <>
                                                    <select
                                                        value={member.role}
                                                        onChange={(e) => updateMemberRole(member.id, e.target.value)}
                                                        disabled={loading}
                                                    >
                                                        <option value="ROOMMATE">Roommate</option>
                                                        <option value="ASSISTANT">Assistant</option>
                                                        <option value="GUEST">Guest</option>
                                                    </select>
                                                    <button
                                                        className="btn btn-danger"
                                                        style={{ marginLeft: '8px' }}
                                                        onClick={() => removeMember(member.id)}
                                                        disabled={loading}
                                                    >
                                                        Remove
                                                    </button>
                                                </>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Rooms; 