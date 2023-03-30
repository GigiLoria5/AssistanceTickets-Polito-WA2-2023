import {Alert, Button, Card} from "react-bootstrap";

function DisplayProfile(props){
    const {id,email,name,surname,phoneNumber,address,city,country}= props.profile;
    return (
        <Card className="mt-3" >
            <Card.Body >
                <Card.Title>{name} {surname}</Card.Title>
                <Card.Text>{email}</Card.Text>
                <Card.Text>{phoneNumber}</Card.Text>
                <Card.Text>{country}</Card.Text>
                <Card.Text>{city}</Card.Text>
                <Card.Text>{address}</Card.Text>
                <Button  variant="primary">Edit</Button>
            </Card.Body>

        </Card>
    )
}

export default DisplayProfile