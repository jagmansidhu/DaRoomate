import React, { useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import axios from 'axios';

const RoomDetails = ({
                         show,
                         onClose,
                         room,
                         onLeaveRoom,
                         onDeleteRoom,
                         onManageRolesClick,
                         onRemoveMember
                     }) => {
    const { user, getAccessTokenSilently } = useAuth0();
    const [showInviteModal, setShowInviteModal] = useState(false);
    const [inviteEmail, setInviteEmail] = useState('');
    const [inviteStatus, setInviteStatus] = useState('');

    if (!show || !room) return null;

    const isHeadRoommate = room.headRoommateId === user?.sub;

    const handleInviteUser = async () => {
        try {
            const token = await getAccessTokenSilently();
            const response = await axios.post(
                `${process.env.REACT_APP_BASE_API_URL}/api/rooms/invite`,
                { email: inviteEmail, roomId: room.id },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            );

            if (response.status === 200) {
                setInviteStatus('Invite sent successfully!');
                setInviteEmail('');
            } else {
                setInviteStatus('Failed to send invite.');
            }
        } catch (error) {
            console.error(error);
            setInviteStatus('Failed to send invite.');
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal modal-large">
                <div className="modal-header">
                    <h2>{room.name}</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>

                <div className="room-details">
                    <div className="detail-section">
                        <h3>Room Information</h3>
                        <p><strong>Address:</strong> {room.address}</p>
                        <p><strong>Description:</strong> {room.description}</p>
                        <p><strong>Room Code:</strong> <code>{room.roomCode}</code></p>
                    </div>

                    <div className="detail-section">
                        <h3>Members</h3>
                        <div className="members-list">
                            {room.members?.map((member) => {
                                const isSelf = member.userId === user?.sub;
                                return (
                                    <div key={member.id} className="member-item">
                                        <div className="member-info">
                                            <span className="member-name">{member.name}</span>
                                            <span className="member-role">{member.role}</span>
                                        </div>
                                        {isSelf && member.role !== 'HEAD_ROOMMATE' && (
                                            <button
                                                className="btn btn-danger"
                                                onClick={() => onLeaveRoom(member.id)}
                                            >
                                                Leave Room
                                            </button>
                                        )}
                                        {isHeadRoommate && !isSelf && (
                                            <button
                                                className="btn btn-danger"
                                                onClick={() => onRemoveMember(member.id)}
                                            >
                                                Remove
                                            </button>
                                        )}
                                    </div>
                                );
                            })}
                        </div>
                    </div>

                    {isHeadRoommate && (
                        <div className="detail-section">
                            <h3>Management</h3>
                            <div className="management-actions">
                                <button className="btn btn-primary" onClick={onManageRolesClick}>
                                    Manage Roles
                                </button>
                                <button className="btn btn-danger" onClick={onDeleteRoom}>
                                    Delete Room
                                </button>
                                <button className="btn btn-secondary" onClick={() => setShowInviteModal(true)}>
                                    Invite User
                                </button>
                            </div>
                        </div>
                    )}

                    {/* Invite Modal */}
                    {showInviteModal && (
                        <div className="modal-overlay">
                            <div className="modal">
                                <div className="modal-header">
                                    <h3>Invite a Roommate</h3>
                                    <button className="modal-close" onClick={() => setShowInviteModal(false)}>×</button>
                                </div>
                                <div className="modal-body">
                                    <input
                                        type="email"
                                        className="input"
                                        placeholder="Enter email address"
                                        value={inviteEmail}
                                        onChange={(e) => setInviteEmail(e.target.value)}
                                    />
                                    {inviteStatus && <p>{inviteStatus}</p>}
                                </div>
                                <div className="modal-actions">
                                    <button className="btn" onClick={() => setShowInviteModal(false)}>Cancel</button>
                                    <button className="btn btn-primary" onClick={handleInviteUser}>Send Invite</button>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default RoomDetails;
