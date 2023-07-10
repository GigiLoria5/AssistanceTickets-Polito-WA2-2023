export function User(id, email, name, role) {
    this.id = id; // Null if role is Manager
    this.email = email;
    this.name = name;
    this.role = role;
}
