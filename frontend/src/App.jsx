import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Register from './Register';
import Home from './Home';
import axios from "axios";

axios.defaults.baseURL = 'http://localhost:8085';
export default function App() {

    return (
        <Router>
            <nav>
                <Link to="/">Home</Link> |
                <Link to="/register">Register</Link> | {}
                {}
            </nav>
            <hr />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/register" element={<Register />} /> {}
                {}
            </Routes>
        </Router>
    );
}