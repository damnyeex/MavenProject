export class User {
    constructor({
        id,
        login,
        role,
        lastname,
        firstname,
        middlenamem,
        tenantId,
    }) {
        this.id = id;
        this.login = login;
        this.lastname = lastname;
        this.firstname = firstname;
        this.middlenamem = middlenamem;
        this.role = role;
        this.tenantId = tenantId;
    }

    get isAdmin() {
        return this.role === "ADMIN";
    }
}
