import {useEffect, useState} from 'react';
import Products from "./components/Products";
import Profiles from "./components/Profiles";
import {Row, Col, Spinner, Button,Container} from "react-bootstrap";


function App() {
    return (
        <ProductSupportApp/>
    );
}

function ProductSupportApp() {
    const [productView, setProductView] = useState(true);  // no user is logged in when app loads
    const [profile,setProfile] = useState({});


    function getProfile() {
        setProfile({email:"mock@gmail.com", name:"mock", surname:"supermock", number:"123"})
    }


    return (
        <Container className='mt-3'>
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
                        <Profiles getProfile={getProfile} profile={profile}/>


                }
            </Row>
        </Container>


    );
}


export default App;