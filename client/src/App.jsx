import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Products from "./components/Products";
import Profiles from "./components/Profiles";
import Navbar from "./components/Navbar";
import NotFoundPage from "./components/NotFoundPage";
import {useState} from "react";
import {ROLE_CLIENT, ROLE_EXPERT, ROLE_MANAGER} from "./utils/constants";
import ManagerDashboard from "./components/ManagerDashboard";
import ExpertDashboard from "./components/ExpertDashboard";
import ClientDashboard from "./components/ClientDashboard";

function App() {
    return (
        <Router>
            <Root/>
        </Router>
    );
}

function Root() {
    const [loggedIn, setLoggedIn] = useState(false);
    const [userInfo, setUserInfo] = useState(false);

    // TODO: once we have login remove hardcoded role
    return (
        <Routes>
            <Route path="/" element={<Navbar/>}>
                <Route index element={renderDashboard(ROLE_MANAGER)}/>
                <Route path='/products' element={<Products/>}/>
                <Route path='/profiles' element={<Profiles/>}/>
            </Route>

            <Route path='*' element={<NotFoundPage/>}/>
        </Routes>
    );
}

function renderDashboard(userRole) {
    switch (userRole) {
        case ROLE_MANAGER:
            return <ManagerDashboard/>;
        case ROLE_EXPERT:
            return <ExpertDashboard/>;
        case ROLE_CLIENT:
            return <ClientDashboard/>;
        default:
            return <NotFoundPage/>;
    }
}

export default App;
