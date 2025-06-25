<template>
  <div class="login-container">
    <div class="login-header">
      <div class="login-icon">
        <i class="el-icon-user-solid"></i>
      </div>
      <h2 class="login-title">欢迎回家</h2>
    </div>

    <el-form ref="loginForm" :model="loginForm" :rules="rules" class="login-form">
      <el-form-item prop="username">
        <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="el-icon-user">
        </el-input>
      </el-form-item>

      <el-form-item prop="password">
        <el-input type="password" v-model="loginForm.password" placeholder="密码" prefix-icon="el-icon-lock">
        </el-input>
      </el-form-item>

      <el-form-item>
        <div class="validate-container">
          <el-input v-model="loginForm.validCode" placeholder="验证码" prefix-icon="el-icon-key">
          </el-input>
          <ValidCode @input="createValidCode" class="validate-code" />
        </div>
      </el-form-item>

      <div class="login-options">
        <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
        <el-link type="success" @click="$router.push('/forget')" class="forget-link">
          忘记密码？
        </el-link>
      </div>

      <el-form-item>
        <el-button type="success" class="login-button" @click="onLogin">
          <i class="el-icon-right"></i>
          开启田园时光
        </el-button>
      </el-form-item>

      <div class="login-actions">
     
        <el-link type="success" @click="toRegister" class="register-link">
          注册账号
        </el-link>
      </div>
    </el-form>
  </div>
</template>

<script>
import ValidCode from "../components/Validate";
import request from "@/utils/request";
import { setRoutes } from "@/router";

export default {
  name: 'Login',
  components: {
    ValidCode
  },
  data() {
    return {
      validCode: '', //通过valicode获取的验证码
      loginForm: {
        username: '',
        password: '',
        validCode: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
        ]
      },
      rememberMe: false
    };
  },
  mounted() {
    this.loadRememberedCredentials();
  },
  methods: {
    toRegister() {
      this.$router.push("/register");
    },
    createValidCode(data) {
      this.validCode = data
    },
    loadRememberedCredentials() {
      const remember = localStorage.getItem('rememberMe') === 'true';
      if (remember) {
        this.rememberMe = true;
        this.loginForm.username = localStorage.getItem('username') || '';
        this.loginForm.password = localStorage.getItem('password') || '';
      }
    },
    onLogin() {
      this.$refs.loginForm.validate((valid) => {
        if (valid) {
          // 验证码比较时转换为小写，实现不区分大小写
          if (this.loginForm.validCode.toLowerCase() !== this.validCode.toLowerCase()) {
            this.$message.error("验证码错误");
            return;
          }
          request.post("/user/login", this.loginForm)
            .then(res => {
              if (res.code === "0") {
                this.$message({
                  message: "登录成功",
                  type: "success",
                  duration: 1000,
                  showClose: true
                })
                if (res.data.token) {
                  localStorage.setItem("token", res.data.token);
                }
                if (res.data) {
                  if (res.data.role === 'USER') {
                    localStorage.setItem("frontUser", JSON.stringify(res.data));
                  } else {
                    localStorage.setItem("backUser", JSON.stringify(res.data));
                  }
                }

                // 根据用户角色决定跳转路径
                if (res.data.role !== 'USER') {
                  if (res.data.menuList) {
                    localStorage.setItem("userMenuList", JSON.stringify(res.data.menuList));
                  }
                  setRoutes();
                  this.$router.push('/showView');
                } else {
                  this.$router.push('/');
                }

                if (this.rememberMe) {
                  localStorage.setItem('rememberMe', 'true');
                  localStorage.setItem('username', this.loginForm.username);
                  localStorage.setItem('password', this.loginForm.password);
                } else {
                  localStorage.removeItem('rememberMe');
                  localStorage.removeItem('username');
                  localStorage.removeItem('password');
                }
              } else {
                this.$message.error(res.msg);
              }
            })
            .catch(error => {
              console.error("登录失败:", error);
            });
        } else {
          return false;
        }
      });
    }
  }
};
</script>

<style scoped>
.login-container {
  width: 100%;
  max-width: 420px;
  animation: fadeIn 0.6s ease-out;
  margin: 0 auto;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #4a8c38 0%, #56a032 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  box-shadow: 0 10px 20px rgba(74, 140, 56, 0.2);
}

.login-icon i {
  color: white;
  font-size: 32px;
}

.login-title {
  font-size: 28px;
  color: #2c3e50;
  margin-bottom: 8px;
  font-weight: 600;
}

.login-subtitle {
  font-size: 16px;
  color: #7f8c8d;
}

.login-form {
  margin-top: 20px;
}

.validate-container {
  display: flex;
  gap: 16px;
}

.validate-code {
  flex-shrink: 0;
  width: 120px;
  height: 46px;
  border-radius: 8px;
  overflow: hidden;
  /* box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); */
}

.login-button {
  width: 100%;
    height: 42px;
    font-size: 16px;
    font-weight: 500;
    letter-spacing: 1px;
    margin-top: 20px;
    border-radius: 8px;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

.login-button i {
  margin-right: 8px;
}

.login-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 24px;
  font-size: 15px;
  color: #7f8c8d;
}

.divider {
  width: 1px;
  height: 16px;
  background-color: #ddd;
  margin: 0 15px;
}

/* Element UI 组件样式覆盖 */
/* Element UI 组件样式覆盖 */
:deep(.el-input__inner) {
    height: 42px;
    line-height: 42px;
    border-radius: 8px;
    border: 1px solid #dcdfe6;
    transition: all 0.3s ease;
}

:deep(.el-input__inner:focus) {
    border-color: #67C23A;
    box-shadow: 0 0 0 2px rgba(103, 194, 58, 0.2);
}

:deep(.el-form-item) {
    margin-bottom: 24px;
}

:deep(.el-input__prefix) {
    left: 12px;
    top: 0;
    height: 100%;
    display: flex;
    align-items: center;
}

:deep(.el-input__prefix i) {
    font-size: 16px;
    line-height: 1;
    display: flex;
    align-items: center;
}

:deep(.el-input__inner) {
    padding-left: 38px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式调整 */
@media (max-width: 480px) {
  .login-container {
    padding: 0 20px;
  }
  
  .login-icon {
    width: 60px;
    height: 60px;
  }
  
  .login-icon i {
    font-size: 28px;
  }
  
  .login-title {
    font-size: 24px;
  }
  
  .login-subtitle {
    font-size: 14px;
  }
  
  :deep(.el-input__inner) {
    height: 42px;
    line-height: 42px;
    font-size: 14px;
  }
  
  .login-button {
    height: 42px;
    font-size: 16px;
  }
  
  .validate-container {
    gap: 10px;
  }
  
  .validate-code {
    width: 100px;
  }
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 16px 0 24px;
}

:deep(.el-checkbox__label) {
  color: #606266;
  font-size: 13px;
}

:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: #67C23A;
  border-color: #67C23A;
}

:deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
  color: #67C23A;
}
</style>