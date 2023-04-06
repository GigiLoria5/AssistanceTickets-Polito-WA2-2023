import Products from "./components/Products";
import Profiles from "./components/Profiles";
import {Row, Col, Button } from "react-bootstrap";
import {BrowserRouter as Router, Routes, Route, useNavigate} from 'react-router-dom';
import CommonTopSide from "./components/CommonTopSide";

function App() {
    return (
        <Router>
            <ProductSupportApp/>
        </Router>
    );
}

function ProductSupportApp() {
    return (
        <Routes>
            <Route path="/" element={<CommonTopSide/>}>
                <Route index element={<Home/>}/>
                <Route path='/products' element={<Products/>}/>
                <Route path='/profiles' element={<Profiles/>}/>
                <Route path="*" element={<NoMatch/>}/>
            </Route>
        </Routes>
    );
}


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

function NoMatch() {

    const navigate = useNavigate();
    return (
        <>
            <Row className="mb-5">
                <Col md="auto">
                    <h1>Questa pagina non esiste</h1>
                </Col>
            </Row>
            <Row>
                <Col>
                    <Button variant="primary" onClick={() => navigate('/')}>Torna alla home</Button>
                </Col>
            </Row>
        </>
    );
}

export default App;