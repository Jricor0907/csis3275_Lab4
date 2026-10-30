<template>
  <div class="login-container">
    <div class="edit-form">
      <h4>Login</h4>

      <form @submit.prevent="login">
        <div>
          <label for="studentId">Student ID:</label>
          <input type="text" id="studentId" v-model="studentLoginRequest.studentId" />
        </div>

        <div>
          <label for="password">Password:</label>
          <input type="password" id="password" v-model="studentLoginRequest.password" />
        </div>

        <div class="button-group">
          <button type="submit">Login</button>
          <button type="button" @click="openCreateModal">Create</button>
          <button type="button" @click="removeAccount" class="remove-btn">Remove</button>
        </div>
      </form>

      <p v-if="message" class="error">{{ message }}</p>
    </div>

    <div v-if="showModal" class="modal-overlay">
      <div class="modal-card">
        <h4>Create Student Account</h4>

        <form @submit.prevent="submitRegistration">
          <div>
            <label>Student ID:</label>
            <input v-model="newStudent.studentId" required />
          </div>

          <div>
            <label>Password:</label>
            <input type="password" v-model="newStudent.password" required />
          </div>

          <div>
            <label>Full Name:</label>
            <input v-model="newStudent.name" required />
          </div>

          <div>
            <label>Email:</label>
            <input type="email" v-model="newStudent.email" required />
          </div>

          <div class="button-group">
            <button type="submit">Submit</button>
            <button type="button" @click="closeCreateModal">Cancel</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import LoginService from '../services/LoginService'
export default {
  name: 'StudentLogin',

  data() {
    return {
      studentLoginRequest: {
        studentId: '',
        password: '',
      },

      message: '',
      showModal: false,

      newStudent: {
        studentId: '',
        password: '',
        name: '',
        email: '',
      },
    }
  },

  methods: {
    async login() {
      this.message = ''

      try {
        const response = await LoginService.login(
          this.studentLoginRequest.studentId,
          this.studentLoginRequest.password,
        )

        localStorage.setItem('sid', this.studentLoginRequest.studentId)

        alert(response.data)
      } catch (error) {
        if (error.response) {
          this.message = error.response.data
        } else {
          this.message = 'Cannot connect to the server.'
        }
      }
    },

    async removeAccount() {
      this.message = ''

      if (!this.studentLoginRequest.studentId || !this.studentLoginRequest.password) {
        alert('Please enter Student ID and password.')
        return
      }

      if (
        !confirm(`Are you sure you want to remove account: ${this.studentLoginRequest.studentId}?`)
      ) {
        return
      }

      try {
        const response = await LoginService.remove(this.studentLoginRequest)

        alert(
          response.data.message +
            '\nStudent ID: ' +
            response.data.studentId +
            '\nFull Name: ' +
            response.data.name +
            '\nEmail: ' +
            response.data.email,
        )

        localStorage.removeItem('sid')
        this.studentLoginRequest.studentId = ''
        this.studentLoginRequest.password = ''
        this.message = ''
      } catch (error) {
        if (error.response) {
          alert(error.response.data.message)
        } else {
          alert('Cannot connect to the server.')
        }
      }
    },
    openCreateModal() {
      this.showModal = true
    },

    closeCreateModal() {
      this.showModal = false
      this.newStudent = {
        studentId: '',
        password: '',
        name: '',
        email: '',
      }
    },
    async submitRegistration() {
      this.message = ''

      try {
        const response = await LoginService.register(this.newStudent)

        alert(response.data)

        this.closeCreateModal()
      } catch (error) {
        if (error.response) {
          alert(error.response.data)
        } else {
          alert('Cannot connect to the server.')
        }
      }
    },
  },
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #e8eef5;
}

.edit-form,
.modal-card {
  width: 320px;
  padding: 25px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.edit-form div,
.modal-card div {
  margin-bottom: 12px;
}

label {
  display: block;
  margin-bottom: 5px;
}

input {
  width: 100%;
  box-sizing: border-box;
  padding: 8px;
}

.button-group {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

button {
  flex: 1;
  padding: 8px;
  cursor: pointer;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.error {
  color: red;
}
</style>
