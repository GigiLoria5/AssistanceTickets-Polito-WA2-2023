import {Form, Button, Alert} from "react-bootstrap";
import {useState} from "react";
import {Profile} from "../utils/Profile";



function FormProfile(props){
    const [errorMsg,setErrorMsg] = useState('');
    const [email,setEmail] = useState('');
    const [name,setName] = useState('');
    const[surname,setSurname] = useState('');
    const[phoneNumber,setPhoneNumber] = useState('');
    const[address,setAddress] = useState('');
    const[city,setCity] = useState('');
    const[country,setCountry] = useState('');

    function validateEmail(input){
        const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
        return emailPattern.test(input);
    }
    function validatePhone(input) {
        const regex = /^[0-9+-]+$/;
        return regex.test(input);
    }
    const checkEmpty = (input) => input.trim().length===0;
    const handleSubmit = (event) => {
        event.preventDefault();

        if(!validateEmail(email))
            setErrorMsg("Email format not valid")
        else if(checkEmpty(email))
            setErrorMsg("Email can't be empty")
        else if(checkEmpty(name))
            setErrorMsg("Name can't be empty")
        else if(checkEmpty(surname))
            setErrorMsg("Surname can't be empty")
        else if(checkEmpty(phoneNumber))
            setErrorMsg("Phone number can't be empty")
        else if(!validatePhone(phoneNumber))
            setErrorMsg("Phone number must be only (0-9,+,-)")
        else if(checkEmpty(address))
            setErrorMsg("Address can't be empty")
        else if(checkEmpty(city))
            setErrorMsg("City can't be empty")
        else if(checkEmpty(country))
            setErrorMsg("Country can't be empty")
        else {
            const newProfile = new Profile(email, name, surname, phoneNumber, address, city, country);
            props.addProfile(newProfile);
            props.changeVisible();
        }
    }
    return(
        <>
        {errorMsg ? <Alert variant='danger' onClose={() => setErrorMsg('')}  className="roundedError" dismissible>{errorMsg}</Alert> : false}
        <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="formBasicEmail">
                <Form.Label>Email address</Form.Label>
                <Form.Control type="email" onChange={ev=>setEmail(ev.target.value)}/>
            </Form.Group>
            <Form.Group className="mb-3" >
                <Form.Label>Name</Form.Label>
                <Form.Control onChange={ev=>setName(ev.target.value)}/>
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>Surname</Form.Label>
                <Form.Control onChange={ev=>setSurname(ev.target.value)}/>
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>Phone number</Form.Label>
                <Form.Control maxLength={15} onChange={ev=>setPhoneNumber(ev.target.value)}/>
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>Country</Form.Label>
                <Form.Control onChange={ev=>setCountry(ev.target.value)}/>
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>City</Form.Label>
                <Form.Control  onChange={ev=>setCity(ev.target.value)}/>
            </Form.Group>

            <Form.Group className="mb-3" >
                <Form.Label>Address</Form.Label>
                <Form.Control  onChange={ev=>setAddress(ev.target.value)}/>
            </Form.Group>

            <Button  variant="primary" type="submit">
                Add
            </Button>
            <Button variant="secondary" onClick={props.changeVisible}>Cancel</Button>
        </Form>
        </>
    );
}

export default FormProfile