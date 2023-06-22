import Products from "./components/Products";
import Profiles from "./components/Profiles";
import {Button, Col, Row} from "react-bootstrap";
import {BrowserRouter as Router, Link, Route, Routes, useNavigate} from 'react-router-dom';
import CommonTopSide from "./components/CommonTopSide";

function App() {
    return (
        <Router>
            <Root/>
        </Router>
    );
}

function Root() {
    return (
        <Routes>
            <Route path="/" element={<CommonTopSide/>}>
                <Route index element={<Home/>}/>
                <Route path='/products' element={<Products/>}/>
                <Route path='/profiles' element={<Profiles/>}/>
            </Route>

            <Route path='*'
                   element={<><h1>Oh no! Page not found.</h1> <p>Return to our <Link to="/">homepage</ Link>. </p></>}
            />
        </Routes>
    );
}

// TODO: remove this
function Home() {
    const navigate = useNavigate();

    return (
        <Row>
            <Col className="d-flex justify-content-center">
                <Button variant="primary" className="mx-2" onClick={() => navigate('/products')}>products</Button>
                <Button variant="primary" onClick={() => navigate('/profiles')}>profiles</Button>
            </Col>
        </Row>
    )
}

export default App;
