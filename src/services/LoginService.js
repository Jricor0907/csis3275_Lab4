
import axios from 'axios';

const API_URL = 'http://localhost:8080/api';

class LoginService {

  login(studentId, password) {
    return axios.post(`${API_URL}/login`, {
      studentId,
      password
    });
  }

  register(student) {
    return axios.post(`${API_URL}/register`, student);
  }

}

export default new LoginService();
