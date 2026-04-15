export class User {
    constructor({ id, login, role, tenantId }) {
        this.id = id;
        this.login = login;
        this.role = role;
        this.tenantId = tenantId;
    }

    get isAdmin() {
        return this.role === "ADMIN";
    }
}
