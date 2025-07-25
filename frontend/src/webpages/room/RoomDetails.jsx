import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';


const RoomDetails = ({
                         show,
                         onClose,
                         room,
                         onLeaveRoom,
                         onDeleteRoom,
                         onManageRolesClick,
                         onRemoveMember,
                     }) => {
    const { user } = useAuth0();

    if (!show || !room) return null;

    const isHeadRoommate = room.headRoommateId === user?.sub;

    return (
        <div className="modal-overlay">
            <div className="modal modal-large">
                <div className="modal-header">
                    <h2>{room.name}</h2>
                    <button className="modal-close" onClick={onClose}>
                        ×
                    </button>
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

                                        {/* Leave button for current user if not head roommate */}
                                        {isSelf && member.role !== 'HEAD_ROOMMATE' && (
                                            <button
                                                className="btn btn-danger"
                                                onClick={() => onLeaveRoom(member.id)}
                                            >
                                                Leave Room
                                            </button>
                                        )}

                                        {/* Remove button if you're the head roommate and it's not you */}
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
                                <button
                                    className="btn btn-primary"
                                    onClick={onManageRolesClick}
                                >
                                    Manage Roles
                                </button>
                                <button
                                    className="btn btn-danger"
                                    onClick={onDeleteRoom}
                                >
                                    Delete Room
                                </button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default RoomDetails;
