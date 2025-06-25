<template>
  <div class="app-wrapper">
    <div class="side-container">
      <div class="logo-container">
        <i class="el-icon-shopping-cart-2 logo-icon"></i>
        <div class="logo-text-container">
          <h1 class="logo-text">生鲜到家</h1>
          <div class="logo-subtitle">后台管理系统</div>
        </div>
      </div>
      <SideMenu ref="sideMenu" />
    </div>
    <div class="main-container">
      <div class="main-header">
        <HeaderBar />
      </div>
      <div class="main-content">
        <el-scrollbar wrap-class="scrollbar">
          <router-view @update:user="updateUser" />
        </el-scrollbar>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderBar from '../components/Header.vue'
import SideMenu from '../components/Aside.vue'

export default {
  name: 'Layout',
  data() {
    return {
      userInfo: JSON.parse(localStorage.getItem("backUser") || {}),
    };
  },
  created() {
// 判断是否登录
    if(!this.userInfo){
      this.$message.warning('请先登录')
      this.$router.push('/login')
    }

  },
  provide() {
    return {
      userInfo: this.userInfo,
    
    };
  },
  components: { HeaderBar, SideMenu },
  computed: {

  },
  methods: {
    updateUser(user) {
      this.userInfo = user;
      this.$refs.sideMenu.refreshMenu();
    }

  }
}
</script>

<style lang="less">
@import "../assets/less/scroller-bar";

.app-wrapper {
  width: 100%;
  height: 100%;
  overflow: hidden;
  background-color: #f5f7fa;

  .side-container {
    box-shadow: 2px 0 8px rgba(0,0,0,0.08);
    background-color: #fff;
    float: left;
    width: 220px;
    height: 100vh;
    overflow-y: auto;
    overflow-x: hidden;
    // 滚动条宽度
    scrollbar-width: thin;
    scrollbar-color: #3AB777 #f0f0f0;
    border-right: 1px solid #f0f0f0;
    position: relative;
    z-index: 2;

    .logo-container {
      height: 64px;
      background: linear-gradient(to right, #3AB777, #4FC08D);
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 24px;
      gap: 12px;
      position: relative;
      overflow: hidden;
      
      &:before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 1px;
        background: linear-gradient(to right, 
          rgba(255,255,255,0.1),
          rgba(255,255,255,0.3),
          rgba(255,255,255,0.1)
        );
      }
      
      .logo-icon {
        font-size: 28px;
        color: #fff;
        position: relative;
        z-index: 1;
    
        margin-right: 0;
      }
      
      .logo-text-container {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
      }
      
      .logo-text {
        margin: 0;
        font-size: 20px;
        color: #fff;
        font-weight: 600;
        letter-spacing: 1px;
        white-space: nowrap;
        position: relative;
        z-index: 1;
        text-shadow: 0 2px 4px rgba(0,0,0,0.1);
        line-height: 1.2;
      }
      
      .logo-subtitle {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.85);
        font-weight: 400;
        letter-spacing: 0.5px;
        white-space: nowrap;
        margin-top: 2px;
      }
    }
  }

  .main-container {
    margin-left: 220px;
    min-height: 100vh;
    background-color: #f5f7fa;
    padding: 16px;
    position: relative;
    overflow: hidden;

    .main-header {
      background: #fff;
      border-radius: 8px;
      margin-bottom: 16px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.04);
      padding: 16px;
      
      .el-header {
        padding: 0;
        height: auto;
      }
    }

    .main-content {
      background: #fff;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.04);
      min-height: calc(100vh - 140px);
      
      .el-scrollbar {
        height: calc(100vh - 140px);
        
        .scrollbar {
          height: 100%;
          overflow-x: hidden;
          padding: 20px;
        }
      }
    }
  }
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .logo-subtitle {
    display: none; /* 在小屏幕上隐藏副标题 */
  }
}
</style>