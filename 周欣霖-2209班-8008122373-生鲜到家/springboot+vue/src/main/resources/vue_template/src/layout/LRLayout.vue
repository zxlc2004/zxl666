<template>
    <div class="auth-container">
        <div class="bg-image"></div>
        <div class="bg-overlay"></div>
        <div class="auth-header">
            <div class="logo" @click="goToHome">
                <i class="el-icon-shopping-cart-2"></i>
                <span class="brand-name">生鲜到家</span>
            </div>
            <el-button type="success" class="home-btn" @click="goToHome">
                <i class="el-icon-s-home"></i> 返回首页
            </el-button>
        </div>
        
        <div class="auth-content">
            <div class="auth-card">
                <div class="brand-tagline">
                    <div class="tagline-content">
                        <h2>自然馈赠 • 匠心呈现</h2>
                        <div class="features">
                            <div class="feature-item">
                                <i class="el-icon-shopping-cart-2"></i>
                                <span>甄选之约</span>
                            </div>
                            <div class="feature-item">
                                <i class="el-icon-box"></i>
                                <span>原乡直采</span>
                            </div>
                            <div class="feature-item">
                                <i class="el-icon-truck"></i>
                                <span>鲜达万家</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="auth-card-content">
                    <router-view />
                </div>
            </div>
        </div>
        <div class="auth-footer">
            <p>© 2025 生鲜到家农业平台 - 让自然滋养生活</p>
        </div>
    </div>
</template>

<script>
export default {
    name: 'LRLayout',
    methods: {
        goToHome() {
            this.$router.push('/')
        }
    }
}
</script>

<style scoped>
.auth-container {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    position: relative;
    overflow: hidden;
}

.bg-image {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: url('@/assets/imgs/lb1.webp');
    background-size: cover;
    background-position: center;
    filter: blur(8px);
    transform: scale(1.1); /* 避免模糊边缘 */
    z-index: 0;
}

.bg-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(135deg, 
        rgba(255, 255, 255, 0.92),
        rgba(235, 245, 235, 0.88),
        rgba(220, 235, 220, 0.85)
    );
    z-index: 1;
}

.auth-header {
    position: relative;
    z-index: 2;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px 40px;
    backdrop-filter: blur(10px);
    background: linear-gradient(to right,
        rgba(255, 255, 255, 0.95),
        rgba(240, 255, 240, 0.92)
    );
    box-shadow: 0 1px 20px rgba(0, 0, 0, 0.05);
}

.logo {
    cursor: pointer;
    font-size: 22px;
    font-weight: 600;
    color: #67C23A;
    letter-spacing: 2px;
    position: relative;
    display: flex;
    align-items: center;
    gap: 8px;
}

.logo i {
    font-size: 24px;
    color: #67C23A;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.logo:hover i {
    transform: rotate(-12deg) scale(1.1);
    filter: drop-shadow(0 2px 4px rgba(103, 194, 58, 0.2));
}

.home-btn {
    padding: 10px 16px;
    border-radius: 30px;
    font-weight: 500;
    transition: all 0.3s ease;
    background: linear-gradient(45deg, #67C23A, #85ce61);
    backdrop-filter: blur(10px);
    border: none;
    color: white;
    box-shadow: 0 2px 8px rgba(103, 194, 58, 0.2);
}

.home-btn:hover {
    background: linear-gradient(45deg, #85ce61, #67C23A);
    color: white;
    box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
    transform: translateY(-1px);
}

.home-btn i {
    margin-right: 6px;
    font-size: 16px;
}

.auth-content {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 48px 24px;
    position: relative;
    z-index: 2;
    transition: all 0.8s ease;
}

.auth-card {
    display: flex;
    width: 900px;
    border-radius: 24px;
    overflow: hidden;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    background-color: white;
    position: relative;
    transition: all 0.3s ease;
    backdrop-filter: blur(20px);
}

.auth-card:hover {
    /* transform: translateY(-5px); */
    box-shadow: 0 25px 80px rgba(0, 0, 0, 0.2);
}

.auth-card-content {
    width: 50%;
    padding: 40px;
    display: flex;
    justify-content: center;
    align-items: center;
    background: white;
}

.brand-tagline {
    width: 50%;
    background: linear-gradient(135deg, #4a8c38 0%, #56a032 100%);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
}

.brand-tagline::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-image: 
        radial-gradient(circle at 10px 10px, rgba(255, 255, 255, 0.05) 2px, transparent 0),
        radial-gradient(circle at 25px 25px, rgba(255, 255, 255, 0.05) 2px, transparent 0);
    background-size: 30px 30px;
    opacity: 1;
    z-index: 0;
}

.brand-tagline::after {
    content: '\e790';
    font-family: element-icons !important;
    position: absolute;
    bottom: -60px;
    right: -60px;
    font-size: 200px;
    opacity: 0.05;
    transform: rotate(-15deg);
    z-index: 0;
}

.tagline-content {
    position: relative;
    z-index: 1;
    padding: 60px 40px;
    text-align: center;
  /* 新增背景图样式 */
  background-image: url('@/assets/imgs/lb1.webp'); /* 替换为你的图片路径 */
  background-size: cover; /* 背景图覆盖整个容器 */
  background-position: center; /* 背景图居中 */
  background-repeat: no-repeat; /* 禁止重复 */
}

.tagline-content h2 {
    font-size: 28px;
    font-weight: 600;
    margin-bottom: 40px;
    letter-spacing: 1px;
    position: relative;
    display: inline-block;
}

.tagline-content h2::after {
    content: '';
    position: absolute;
    bottom: -10px;
    left: 50%;
    transform: translateX(-50%);
    width: 50px;
    height: 3px;
    background-color: rgba(255, 255, 255, 0.5);
    border-radius: 3px;
}

.features {
    display: flex;
    justify-content: center;
    gap: 30px;
    margin-top: 150px;
}

.feature-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
}

.feature-item i {
    font-size: 32px;
    background: rgba(255, 255, 255, 0.15);
    border-radius: 50%;
    width: 60px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.3s ease;
}

.feature-item:hover i {
    background: rgba(255, 255, 255, 0.25);
    transform: translateY(-5px);
}

.feature-item span {
    font-size: 16px;
    font-weight: 500;
}

.auth-footer {
    position: relative;
    z-index: 2;
    padding: 20px 0;
    text-align: center;
    color: #2d5a1d;
    font-size: 14px;
    background: linear-gradient(to right,
        rgba(255, 255, 255, 0.5),
        rgba(240, 255, 240, 0.7)
    );
    backdrop-filter: blur(10px);
    border-top: 1px solid rgba(74, 140, 56, 0.1);
}

@media (max-width: 992px) {
    .auth-card {
        width: 100%;
        flex-direction: column;
    }
    
    .auth-card-content,
    .brand-tagline {
        width: 100%;
    }
    
    .brand-tagline {
        order: -1;
        padding: 30px 0;
    }
    
    .tagline-content {
        padding: 30px 20px;
    }
    
    .features {
        gap: 20px;
    }
    
    .feature-item i {
        width: 50px;
        height: 50px;
        font-size: 24px;
    }
}

@media (max-width: 576px) {
    .auth-header {
        padding: 16px 24px;
    }
    
    .logo {
        font-size: 18px;
    }
    
    .logo:hover {
        opacity: 0.9;
    }
    
    .home-btn {
        padding: 8px 12px;
        font-size: 13px;
    }
    
    .auth-content {
        padding: 24px 16px;
    }
    
    .auth-card-content {
        padding: 30px 20px;
    }
    
    .features {
        flex-direction: column;
        gap: 15px;
    }
    
    .feature-item {
        flex-direction: row;
        justify-content: center;
    }
    
    .feature-item i {
        width: 40px;
        height: 40px;
        font-size: 20px;
        margin-right: 10px;
    }
    
    .logo i {
        font-size: 20px;
    }
}
</style>