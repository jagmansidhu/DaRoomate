import React, { useState, useEffect } from 'react';
import axios from 'axios';

const RoleManagement = ({ show, room, onClose, onUpdate, getAccessTokenSilently }) => {
    const [members, setMembers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (room && room.members) {
            setMembers(room.members);
        } else {
            setMembers([]);
        }
    }, [room]);

    const updateMemberRole = async (memberId, newRole) => {
        setLoading(true);
        setError(null);
        try {
            const accessToken = await getAccessTokenSilently();
            await axios.put(`${process.env.REACT_APP_BASE_API_URL}/api/rooms/${room.id}/members/${memberId}/role`,
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
        } catch (err) {
            console.error('Error updating member role:', err);
            setError('Failed to update member role.');
        } finally {
            setLoading(false);
        }
    };

    const removeMember = async (memberId) => {
        if (!window.confirm('Are you sure you want to remove this member from the room?')) return;
        setLoading(true);
        setError(null);
        try {
            const accessToken = await getAccessTokenSilently();
            await axios.delete(`${process.env.REACT_APP_BASE_API_URL}/api/rooms/${room.id}/members/${memberId}`, {
                headers: { Authorization: `Bearer ${accessToken}` },
            });
            setMembers(members.filter((m) => m.id !== memberId));
            onUpdate(); // Trigger a refresh
        } catch (err) {
            console.error('Error removing member:', err);
            setError('Failed to remove member.');
        } finally {
            setLoading(false);
        }
    };

    if (!show || !room) return null;

    return (
        <div className="modal-overlay">
            <div className="modal modal-large">
                <div className="modal-header">
                    <h2>Manage Roles - {room.name}</h2>
                    <button className="modal-close" onClick={onClose}>×</button>
                </div>
                {error && <div className="alert alert-error">{error}</div>}
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

export default RoleManagement;