import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";

const VerifyHandler = () => {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const verifyEmail = async () => {
            const params = new URLSearchParams(location.search);
            const token = params.get("token");

            if (!token) {
                navigate("/login?error=missing-token", { replace: true });
                return;
            }

            try {
                const res = await axios.get(`/api/verify?token=${token}`);
                if (res.data === true) {
                    navigate("/login?verified=true", { replace: true });
                } else {
                    navigate("/login?error=verify-failed", { replace: true });
                }
            } catch (err) {
                console.error("Verification request failed", err);
                navigate("/login?error=server", { replace: true });
            }
        };

        verifyEmail();
    }, [location, navigate]);

    return (
        <div className="flex items-center justify-center h-screen">
            <p className="text-lg">Verifying your email...</p>
        </div>
    );
};

export default VerifyHandler;
