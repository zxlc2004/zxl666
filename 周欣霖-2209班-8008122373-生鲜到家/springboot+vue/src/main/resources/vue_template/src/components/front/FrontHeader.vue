<template>
  <div class="front-header">
    <div class="header-container">
      <div class="logo-area" @click="$router.push('/')">
        <i class="el-icon-shopping-cart-2"></i>
        <span>生鲜到家</span>
      </div>

      <el-menu mode="horizontal" :router="true" :default-active="activePath" class="nav-menu">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/products">全部商品</el-menu-item>
        <el-menu-item index="/cart">购物车</el-menu-item>
        <el-menu-item index="/order">我的订单</el-menu-item>
        <el-menu-item index="/articles" class="nav-item">
            农业资讯
          </el-menu-item>
      </el-menu>

      <div class="right-section">
        <div class="search-box">
          <el-input 
            v-model="searchKey" 
            placeholder="搜索商品..." 
            @keyup.enter.native="handleSearch"
            class="search-input">
            <i slot="prefix" class="el-icon-search"></i>
          </el-input>
        </div>

        <div class="user-actions">
          <template v-if="!isLoggedIn">
            <el-button type="text" class="action-btn" @click="goToLogin">
              <i class="el-icon-user"></i>
              <span>登录</span>
            </el-button>
          </template>
          <template v-else>
            <el-dropdown trigger="click">
              <div class="user-info">
                <span class="username">{{ userInfo.username }}</span>
                <i class="el-icon-arrow-down el-icon--right"></i>
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="goToUserCenter">
                  <i class="el-icon-user"></i> 个人中心
                </el-dropdown-item>
                <el-dropdown-item @click.native="goToFavorite">
                  <i class="el-icon-star-off"></i> 我的收藏
                </el-dropdown-item>
                <el-dropdown-item divided @click.native="logout">
                  <i class="el-icon-switch-button"></i> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>

          <el-button type="text" class="action-btn admin-btn" @click="goToAdmin">
            <i class="el-icon-s-tools"></i>
            <span>后台</span>
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import router from '@/router';

export default {
  name: 'FrontHeader',
  data() {
    return {
      searchKey: '',
      isLoggedIn: false,
      userInfo: {},
    
    }
  },
  created() {
    this.checkLoginStatus();

  },
  mounted() {

  },
  methods: {
    checkLoginStatus() {
      const userInfo = localStorage.getItem('frontUser');
      if (userInfo) {
        this.isLoggedIn = true;
        this.userInfo = JSON.parse(userInfo);
      }
    },
    goToLogin() {
      this.$router.push('/login')
    },
    goToUserCenter() {
      this.$router.push('/user-center')
    },
    goToFavorite() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.$router.push('/favorite')
    },
    logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('frontUser');
      this.isLoggedIn = false;
      this.userInfo = {};
      this.$message.success('已退出登录');
      this.$router.push('/');
      window.location.reload()
    },
    handleSearch() {
      if (!this.searchKey.trim()) {
        this.$message({
          type: 'warning',
          message: '请输入搜索关键词'
        })
        return
      }

      this.$router.push({
        path: '/search',
        query: {
          keyword: this.searchKey.trim()
        }
      })

      this.searchKey = ''
    },
    goToAdmin() {
      // 检查是否已登录
      const userMenuList = localStorage.getItem('userMenuList');
      const backUser = localStorage.getItem('backUser');
      if (!userMenuList || !backUser) {
        this.$message.warning('请先登录');
        this.$router.push('/login');
        return;
      }
      
      this.$router.push('/showView');
    }
  },
  computed: {
    activePath() {
      return this.$router.currentRoute.fullPath;
    }
  }
}
</script>

<style scoped>
.front-header {
  position: sticky;
  top: 0;
  z-index: 1000;
  background: #fff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  height: 55px;
  position: relative;
  overflow: hidden;
}

.front-header::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, 
    rgba(255, 255, 255, 0.98),
    #fff
  );
  backdrop-filter: blur(10px);
  z-index: -1;
}

/* 添加背景装饰 */
.front-header::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: 
    radial-gradient(circle at 0% 0%, rgba(103, 194, 58, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 100% 0%, rgba(103, 194, 58, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 50% 50%, rgba(103, 194, 58, 0.01) 0%, transparent 50%),
    linear-gradient(90deg, rgba(103, 194, 58, 0.01) 0%, rgba(103, 194, 58, 0.02) 50%, rgba(103, 194, 58, 0.01) 100%);
  opacity: 0.8;
  z-index: -1;
}

.header-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  height: 100%;
  display: flex;
  align-items: center;
  gap: 24px;
  position: relative;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  color: #67C23A;
  font-size: 18px;
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
  background: linear-gradient(120deg, 
    rgba(103, 194, 58, 0.05) 0%, 
    rgba(103, 194, 58, 0.1) 50%, 
    rgba(103, 194, 58, 0.05) 100%
  );
  position: relative;
  overflow: hidden;
}

.logo-area::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.logo-area:hover {
  background: linear-gradient(120deg, 
    rgba(103, 194, 58, 0.1) 0%, 
    rgba(103, 194, 58, 0.15) 50%, 
    rgba(103, 194, 58, 0.1) 100%
  );
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.1);
}

.logo-area:hover::before {
  opacity: 1;
}

.logo-area i {
  font-size: 22px;
  background: linear-gradient(45deg, #67C23A, #85ce61);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  filter: drop-shadow(0 1px 2px rgba(103, 194, 58, 0.2));
}

.nav-menu {
  border: none;
  background: transparent;
  height: 55px;
  line-height: 55px;
  flex: 1;
  position: relative;
  z-index: 1;
  backdrop-filter: blur(4px);
}

:deep(.el-menu--horizontal > .el-menu-item) {
  height: 55px;
  line-height: 55px;
  padding: 0 16px;
  font-size: 16px;
  border: none !important;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

:deep(.el-menu--horizontal > .el-menu-item:hover) {
  background: linear-gradient(to right, transparent, rgba(103, 194, 58, 0.05), transparent);
}

:deep(.el-menu--horizontal > .el-menu-item.is-active) {
  color: #67C23A;
  font-weight: 500;
  background: linear-gradient(to right, transparent, rgba(103, 194, 58, 0.08), transparent);
}

.right-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.search-box {
  width: 220px;
}

.search-input :deep(.el-input__inner) {
  height: 32px;
  line-height: 32px;
  border-radius: 16px;
  background: #f5f7fa;
  border: 1px solid transparent;
  padding-left: 35px;
  font-size: 13px;
  transition: all 0.3s ease;
}

.search-input :deep(.el-input__inner:focus) {
  background: #fff;
  border-color: #67C23A;
  box-shadow: 0 0 0 2px rgba(103, 194, 58, 0.1);
}

.search-input :deep(.el-input__prefix) {
  left: 12px;
  display: flex;
  align-items: center;
  height: 100%;
}

.search-input :deep(.el-input__prefix i) {
  font-size: 15px;
  color: #909399;
}

.search-input :deep(.el-input__inner:focus + .el-input__prefix i) {
  color: #67C23A;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 下拉菜单样式优化 */
:deep(.el-dropdown-menu) {
  padding: 6px;
  border-radius: 8px;
  border: none;
  box-shadow: 0 3px 15px rgba(0, 0, 0, 0.1);
}

:deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  padding: 10px 16px;
  border-radius: 6px;
  margin: 2px 0;
  line-height: 1;
}

:deep(.el-dropdown-menu__item i) {
  font-size: 15px;
  color: #606266;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

:deep(.el-dropdown-menu__item:hover i) {
  color: #67C23A;
}

:deep(.el-dropdown-menu__item.is-disabled) {
  color: #C0C4CC;
  cursor: not-allowed;
  background-color: transparent;
}

:deep(.el-dropdown-menu__item.el-dropdown-menu__item--divided) {
  border-top: 1px solid #EBEEF5;
  margin: 6px 0;
  padding-top: 12px;
}

:deep(.el-dropdown-menu__item--divided:before) {
  height: 0;
  margin: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-info:hover {
  background: rgba(103, 194, 58, 0.1);
}

.user-info .el-icon-arrow-down {
  font-size: 12px;
  margin-left: 4px;
  transition: transform 0.3s ease;
}

.user-info:hover .el-icon-arrow-down {
  transform: rotate(180deg);
}

.username {
  font-size: 13px;
  color: #606266;
}

.admin-btn {
  color: #409EFF;
}

.admin-btn:hover {
  color: #409EFF;
  background: rgba(64, 158, 255, 0.1);
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .header-container {
    padding: 0 16px;
    gap: 16px;
  }

  .search-box {
    width: 180px;
  }

  :deep(.el-menu--horizontal > .el-menu-item) {
    padding: 0 12px;
  }
}

@media (max-width: 992px) {
  .search-box {
    width: 160px;
  }

  .action-btn span {
    display: none;
  }

  .action-btn {
    padding: 8px;
  }

  .action-btn i {
    margin: 0;
  }
}
</style> 