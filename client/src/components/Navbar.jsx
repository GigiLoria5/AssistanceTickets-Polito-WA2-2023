import {Outlet, useNavigate} from "react-router-dom";
import {Button, Container, Navbar, Row} from "react-bootstrap";

function MyNavbar() {
    const navigate = useNavigate();

    return (
        <>
            <Container>
                <Navbar fixed="top" bg="primary" variant="dark">
                    <Container>
                        <Row xs="auto">
                            <Navbar.Brand title="Home Page" className="asPointer" onClick={() => {
                                navigate('/');
                            }}>
                                Assistance tickets
                            </Navbar.Brand>
                            <Button variant="primary"
                                    onClick={() => navigate('/products')}>products</Button>
                            <Button variant="primary"
                                    onClick={() => navigate('/profiles')}>profiles</Button>
                        </Row>
                    </Container>
                </Navbar>
            </Container>

            <Container className='below-nav'>
                <Outlet/>
            </Container>
        </>
    );
}

export default MyNavbar
