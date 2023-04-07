import {Form, Button, Alert} from "react-bootstrap";
import {useState} from "react";
import {Profile} from "../utils/Profile";



function FormProfile(props){
    const [errorMsg,setErrorMsg] = useState('');
    const [email,setEmail] = useState(props.profile ? props.profile.email : '');
    const [name,setName] = useState(props.profile ? props.profile.name : '');
    const[surname,setSurname] = useState(props.profile ? props.profile.surname : '');
    const[phoneNumber,setPhoneNumber] = useState(props.profile ? props.profile.phoneNumber : '');
    const[address,setAddress] = useState(props.profile ? props.profile.address: '');
    const[city,setCity] = useState(props.profile ? props.profile.city : '');
    const[country,setCountry] = useState(props.profile ? props.profile.country : '');

    function validateEmail(input){
        const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,3}$/;
        return emailPattern.test(input);
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
        else if(checkEmpty(address))
            setErrorMsg("Address can't be empty")
        else if(checkEmpty(city))
            setErrorMsg("City can't be empty")
        else if(checkEmpty(country))
            setErrorMsg("Country can't be empty")
        else {
            const newProfile = new Profile(email, name, surname, phoneNumber, address, city, country);

            if(props.changeEditMode) {
                props.changeEditMode();
                props.addProfile(newProfile,props.profile.email)
            }
            else
                props.addProfile(newProfile);

            props.changeVisible();
        }
    }

    const handleCancel = () =>{
        props.changeVisible();
        if(props.changeEditMode)
            props.changeEditMode();
    }
    return(
        <>
            {props.changeEditMode ? <h1>Edit profile</h1> : <h1>Add profile</h1>}
            {errorMsg ? <Alert variant='danger' onClose={() => setErrorMsg('')}  className="roundedError" dismissible>{errorMsg}</Alert> : false}
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label>Email address</Form.Label>
                    <Form.Control value={email} placeholder="johngreen@group.com" type="email" onChange={ev=>setEmail(ev.target.value)} required/>
                </Form.Group>
                <Form.Group className="mb-3" >
                    <Form.Label>Name</Form.Label>
                    <Form.Control value={name} placeholder="John" onChange={ev=>setName(ev.target.value)} required/>
                </Form.Group>

                <Form.Group className="mb-3" >
                    <Form.Label>Surname</Form.Label>
                    <Form.Control value={surname} placeholder="Green" onChange={ev=>setSurname(ev.target.value)} required/>
                </Form.Group>

                <Form.Group className="mb-3" >
                    <Form.Label>Phone number</Form.Label>
                    <Form.Control
                        type="tel"
                        placeholder="3466281644"
                        maxLength={10}
                        onChange={ev=>setPhoneNumber(ev.target.value)}
                        value={phoneNumber}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3" >
                    <Form.Label>Country</Form.Label>
                    <Form.Control value={country} placeholder="Italy"  onChange={ev=>setCountry(ev.target.value)} required/>
                </Form.Group>

                <Form.Group className="mb-3" >
                    <Form.Label>City</Form.Label>
                    <Form.Control value={city} placeholder="Turin"  onChange={ev=>setCity(ev.target.value)} required/>
                </Form.Group>

                <Form.Group className="mb-3" >
                    <Form.Label>Address</Form.Label>
                    <Form.Control value={address} placeholder="Corso Duca degli Abruzzi, 24" onChange={ev=>setAddress(ev.target.value)} required/>
                </Form.Group>

                <Button  variant="primary" type="submit">
                    Submit
                </Button>
                <Button variant="secondary" onClick={handleCancel}>Cancel</Button>
            </Form>
        </>
    );
}

export default FormProfile