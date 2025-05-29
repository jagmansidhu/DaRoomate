import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Register from './Register';
import Home from './Home';
import Login from './Login';
import axios from "axios";

axios.defaults.baseURL = 'http://localhost:8085';
export default function App() {

    return (
        <Router>
            <nav>
                <Link to="/home">Home</Link> |
                <Link to="/register">Register</Link> |
                <Link to="/login">Login</Link> |
            </nav>
            <hr />
            <Routes>
                <Route path="/home" element={<Home />} />
                <Route path="/register" element={<Register />} />
                <Route path="/Login" element={<Login />} />
            </Routes>
        </Router>
    );
}