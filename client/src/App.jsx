import {useEffect, useState} from 'react';
import Products from "./components/Products";
import Users from "./components/Users";
import {Row, Col, Spinner, Button,Container} from "react-bootstrap";

function App() {
    return (
        <ProductSupportApp/>
    );
}

function ProductSupportApp() {
    const [productView, setProductView] = useState(true);  // no user is logged in when app loads

    return (
        <Container>
        <Row>

            <Button className="roundedButton" variant="outline-danger" onClick={() => setProductView(productView=>!productView)}>
                {
                    productView?"Go to users":"Go to products"
                }
            </Button>
                </Row>

            <Row>

                {
                    productView ?
                        <Products/>
                        :
                        <Users/>

                }
            </Row>
        </Container>


    );
}


export default App;