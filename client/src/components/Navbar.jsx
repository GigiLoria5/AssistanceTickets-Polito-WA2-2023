import {Outlet, useLocation, useNavigate} from "react-router-dom";
import {Container, Nav, Navbar} from "react-bootstrap";

function MyNavbar({userInfo}) {
    const navigate = useNavigate();
    const location = useLocation();

    return (
        <>
            <Container>
                <Navbar fixed="top" bg="primary" variant="dark">
                    <Container>
                        <Navbar.Brand
                            title="Home Page"
                            className="asPointer"
                            onClick={() => {
                                navigate('/');
                            }}>
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                                 className="bi bi-ticket" viewBox="0 0 16 16">
                                <path
                                    d="M0 4.5A1.5 1.5 0 0 1 1.5 3h13A1.5 1.5 0 0 1 16 4.5V6a.5.5 0 0 1-.5.5 1.5 1.5 0 0 0 0 3 .5.5 0 0 1 .5.5v1.5a1.5 1.5 0 0 1-1.5 1.5h-13A1.5 1.5 0 0 1 0 11.5V10a.5.5 0 0 1 .5-.5 1.5 1.5 0 1 0 0-3A.5.5 0 0 1 0 6V4.5ZM1.5 4a.5.5 0 0 0-.5.5v1.05a2.5 2.5 0 0 1 0 4.9v1.05a.5.5 0 0 0 .5.5h13a.5.5 0 0 0 .5-.5v-1.05a2.5 2.5 0 0 1 0-4.9V4.5a.5.5 0 0 0-.5-.5h-13Z"/>
                            </svg>
                            {" "}
                            Assistance Tickets
                        </Navbar.Brand>
                        <Nav className="me-auto">
                            <Nav.Link
                                onClick={() => navigate('/')}
                                active={location.pathname === '/'}
                            >
                                Dashboard
                            </Nav.Link>
                            <Nav.Link
                                onClick={() => navigate('/products')}
                                active={location.pathname === '/products'}
                            >
                                Products
                            </Nav.Link>
                        </Nav>
                        <Nav>
                            <Nav.Link
                                onClick={() => navigate('/profile')}
                                active={location.pathname === '/profile'}
                            >
                                {userInfo ? `${userInfo.name} [${userInfo.role}]` : 'Profile'}
                            </Nav.Link>
                        </Nav>
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
