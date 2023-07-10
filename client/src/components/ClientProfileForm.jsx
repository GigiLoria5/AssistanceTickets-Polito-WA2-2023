import {Button, Container, Form} from "react-bootstrap";
import {useEffect, useState} from "react";
import {Profile} from "../models/Profile";
import {useStatusAlert} from "../hooks/useStatusAlert";
import {useNavigate} from "react-router-dom";
import {
    validateAddress,
    validateCity,
    validateCountry,
    validateEmail,
    validateName,
    validatePhone
} from "../utils/validators";
import {getAccessToken, handleApiError} from "../utils/utils";
import API from "../API";

function ClientProfileForm({profile, onSuccess, onCancel}) {
    const navigate = useNavigate();
    const {StatusAlertComponent, showError} = useStatusAlert();
    const [isLoading, setIsLoading] = useState(false);
    const [email, setEmail] = useState(profile ? profile.email : '');
    const [password, setPassword] = useState("");
    const [name, setName] = useState(profile ? profile.name : '');
    const [surname, setSurname] = useState(profile ? profile.surname : '');
    const [phoneNumber, setPhoneNumber] = useState(profile ? profile.phoneNumber : '');
    const [address, setAddress] = useState(profile ? profile.address : '');
    const [city, setCity] = useState(profile ? profile.city : '');
    const [country, setCountry] = useState(profile ? profile.country : '');
    const editMode = !!profile;
    const profileId = profile ? profile.profileId : null;

    useEffect(() => {
        if (getAccessToken() !== "null" && !profile) {
            navigate("/")
        }
    }, [])

    const handleRegister = (profile) => {
        API.signup(profile, password)
            .then(_ => {
                navigate('/login', {state: {success: true}});
            })
            .catch(err => {
                setIsLoading(false);
                showError(err.error);
            })
    }

    const handleUpdateProfile = (profile) => {
        API.updateProfile(profile)
            .then(_ => {
                onSuccess();
            })
            .catch(err => {
                setIsLoading(false);
                handleApiError(err, showError);
            })
    }

    const handleSubmit = (event) => {
        event.preventDefault();
        if (!validateEmail(email)) {
            showError("Email format not valid")
            return
        }
        if (!validateName(name)) {
            showError("Name format not valid")
            return
        }
        if (!validateName(surname)) {
            showError("Surname format not valid")
            return
        }
        if (!validatePhone(phoneNumber)) {
            showError("Phone number format not valid")
            return
        }
        if (!validateAddress(address)) {
            showError("Address format not valid")
            return
        }
        if (!validateCity(city)) {
            showError("City format not valid")
            return
        }
        if (!validateCountry(country)) {
            showError("Country format not valid")
            return
        }
        setIsLoading(true);
        const profile = new Profile(profileId, email, name, surname, phoneNumber, address, city, country);
        editMode ? handleUpdateProfile(profile) : handleRegister(profile);
    }

    return (
        <div className='color-overlay d-flex justify-content-center align-items-center min-vh-100 p-10 flex-column'>
            <Form className='rounded p-4 p-sm-4 bg-grey' onSubmit={handleSubmit}>
                <h2 className='text-center'>{editMode ? "Edit Profile" : "Client Registration"}</h2>

                {!editMode && <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label>Username</Form.Label>
                    <Form.Control value={email} disabled={isLoading} placeholder="johngreen@group.com" type="email"
                                  onChange={ev => setEmail(ev.target.value)} required/>
                </Form.Group>
                }

                {!editMode &&
                    <Form.Group className="mb-3">
                        <Form.Label>Password</Form.Label>
                        <Form.Control value={password} disabled={isLoading} placeholder={"password"} type="password"
                                      onChange={ev => setPassword(ev.target.value)} required/>
                    </Form.Group>
                }

                <Form.Group className="mb-3">
                    <Form.Label>Name</Form.Label>
                    <Form.Control value={name} disabled={isLoading} placeholder="John"
                                  onChange={ev => setName(ev.target.value)} required/>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Surname</Form.Label>
                    <Form.Control value={surname} disabled={isLoading} placeholder="Green"
                                  onChange={ev => setSurname(ev.target.value)}
                                  required/>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Phone Number</Form.Label>
                    <Form.Control
                        type="tel"
                        disabled={isLoading}
                        placeholder="3466281644"
                        maxLength={10}
                        onChange={ev => setPhoneNumber(ev.target.value)}
                        value={phoneNumber}
                        required
                    />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Country</Form.Label>
                    <Form.Control value={country} disabled={isLoading} placeholder="Italy"
                                  onChange={ev => setCountry(ev.target.value)}
                                  required/>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>City</Form.Label>
                    <Form.Control value={city} disabled={isLoading} placeholder="Turin"
                                  onChange={ev => setCity(ev.target.value)} required/>
                </Form.Group>

                <Form.Group className="mb-3">
                    <Form.Label>Address</Form.Label>
                    <Form.Control value={address} disabled={isLoading} placeholder="Corso Duca degli Abruzzi, 24"
                                  onChange={ev => setAddress(ev.target.value)} required/>
                </Form.Group>

                <Container className="p-0">
                    <div className="d-flex flex-column align-items-center mt-3">
                        <Button variant="primary" className="w-100" type="submit">
                            {isLoading ? "In Progress..." : "Confirm"}
                        </Button>
                    </div>
                    <div className="d-flex flex-column align-items-center mt-3">
                        {editMode
                            ? <Button variant="secondary" className="w-100" onClick={onCancel}>Cancel</Button>
                            :
                            <p>
                                Already have an account?
                                <Button
                                    disabled={isLoading}
                                    variant="link"
                                    onClick={() => navigate("/login")}
                                    className="py-0 pb-2 text-decoration-none">
                                    Login here
                                </Button>
                            </p>
                        }
                    </div>
                </Container>
            </Form>
            <StatusAlertComponent/>
        </div>
    );
}

export default ClientProfileForm
