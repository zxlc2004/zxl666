<template>
  <div class="product-card" @click="handleClick">
    <!-- 骨架屏加载动画 -->
    <div v-if="loading" class="product-skeleton">
      <div class="skeleton-image"></div>
      <div class="skeleton-content">
        <div class="skeleton-line skeleton-title"></div>
        <div class="skeleton-line skeleton-price"></div>
        <div class="skeleton-line skeleton-info"></div>
        <div class="skeleton-line skeleton-actions"></div>
      </div>
    </div>
    
    <!-- 实际内容 -->
    <template v-else>
      <div class="image-container">
        <el-image 
          :src="product.imageUrl?.startsWith('http') ? product.imageUrl : `/api${product.imageUrl}`" 
          class="product-image"
          fit="cover"
          @load="imageLoaded"
          @error="imageError"
        >
          <div slot="error" class="image-slot">
            <i class="el-icon-picture-outline"></i>
          </div>
        </el-image>
        <div class="product-badges">
          <div class="badge new" v-if="product.isNew">新品</div>
          <div class="badge discount" v-if="product.isDiscount==1">特惠</div>
        </div>
      </div>
      
      <div class="product-content">
        <h3 class="product-name">{{ product.name }}</h3>
        
        <div class="product-info">
          <div class="price-section">
            <div class="current-price">
              <span class="symbol">¥</span>
              <span class="amount">{{ product.isDiscount==1 ? product.discountPrice : product.price }}</span>
              <span class="original-price" v-if="product.isDiscount==1">¥{{ product.price }}</span>
            </div>
          </div>
          <div class="stock-info">
            <span class="stock" :class="{ 'low': product.stock < 10 }">
              库存: {{ product.stock }}
            </span>
            <span class="sales">已售{{ product.salesCount }}</span>
          </div>
        </div>
        
        <div class="product-footer">
          <div class="action-group">
            <div class="favorite-btn" 
              :class="{ 'is-favorite': isFavoritePage || product.isFavorite }"
              @click.stop="handleFavorite">
              <i :class="[isFavoritePage ? 'el-icon-delete' : (product.isFavorite ? 'el-icon-star-on' : 'el-icon-star-off')]"></i>
            </div>
            <div class="add-to-cart" @click.stop="handleAddToCart">
              <i class="el-icon-shopping-cart-2"></i>
              <span>加入购物车</span>
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<script>
import Request from '@/utils/request'

export default {
  name: 'ProductCard',
  props: {
    product: {
      type: Object,
      required: true
    },
    isFavoritePage: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      userInfo: JSON.parse(localStorage.getItem('frontUser') || '{}'),
      loading: true,
      imageLoaded: false
    }
  },
  methods: {
    // 图片加载完成
    imageLoaded() {
      setTimeout(() => {
        this.loading = false;
      }, 300);
    },
    
    // 图片加载失败
    imageError() {
      this.loading = false;
    },
    
    isLogin() {
      // 检查是否登录
      const userStr = localStorage.getItem('frontUser')
        if (!userStr) {
          this.$message({
            type: 'warning',
            message: '请先登录'
          })
          this.$router.push('/login')
          return false
        }
        return true
    },
    async handleAddToCart() {
      try {
   
         if(!this.isLogin()){
          return
         }

        const data = {
          userId: this.userInfo.id,
          productId: this.product.id,
          quantity: 1
        }

        const res = await Request.post('/cart', data)
        if (res.code === '0') {
          this.$message({
            type: 'success',
            message: '已添加到购物车'
          })
          this.$emit('add-to-cart', this.product)
        } else {
          this.$message({
            type: 'error',
            message: res.msg || '添加失败'
          })
        }
      } catch (error) {
        console.error('添加到购物车失败:', error)
        this.$message({
          type: 'error',
          message: '添加到购物车失败'
        })
      }
    },

    async handleFavorite() {
      try {
        // 如果是在收藏页面，只触发事件
        if (this.isFavoritePage) {
          this.$emit('toggle-favorite', this.product)
          return
        }

        if(!this.isLogin()){
          return
        }

        const data = {
          userId: this.userInfo.id,
          productId: this.product.id,
          status: this.product.isFavorite ? 1 : 0
        }
        const res = await Request.post('/favorite', data)
        if (res.code === '0') {
          // 使用服务器返回的状态
          this.product.isFavorite = res.data.status === 1
          this.$message({
            type: 'success',
            message: this.product.isFavorite ? '收藏成功' : '已取消收藏'
          })
          // 触发事件并传递最新状态
          this.$emit('toggle-favorite', {
            product: this.product,
            status: res.data.status
          })
          
          // 重新获取收藏状态
          this.getFavoriteStatus()
        } else {
          this.$message({
            type: 'error',
            message: res.msg || '操作失败'
          })
        }
      } catch (error) {
        console.error('收藏操作失败:', error)
        this.$message({
          type: 'error',
          message: '操作失败'
        })
      }
    },

    // 获取收藏状态的方法
    async getFavoriteStatus() {
      if (this.userInfo?.id) {
        try {
          const res = await Request.get(`/favorite/user/${this.userInfo.id}`)
          if (res.code === '0') {
            const favorites = res.data
            this.product.isFavorite = favorites.some(f => f.productId === this.product.id && f.status === 1)
          }
        } catch (error) {
          console.error('获取收藏状态失败:', error)
        }
      }
    },

    handleClick() {
      this.$router.push(`/product/${this.product.id}`)
    },
    
    // 设置一个延迟结束加载状态的方法
    startLoading() {
      this.loading = true;
      // 即使图片加载失败，也最多显示骨架屏2秒
      setTimeout(() => {
        this.loading = false;
      }, 2000);
    }
  },
  created() {
    this.startLoading();
  },
  mounted() {
    // this.product = {
    //   ...this.product,
    //   isFavorite: false
    // }
    
    this.$nextTick(() => {
      this.getFavoriteStatus()
    })
  }
}
</script>

<style scoped>
.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  cursor: pointer;
  min-height: 350px; /* 设置最小高度以保持一致布局 */
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

/* 骨架屏样式 */
.product-skeleton {
  width: 100%;
  height: 100%;
  padding-bottom: 16px;
}

.skeleton-image {
  width: 100%;
  padding-bottom: 100%;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
}

.skeleton-content {
  padding: 12px;
}

.skeleton-line {
  height: 16px;
  margin-bottom: 12px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  border-radius: 4px;
}

.skeleton-title {
  width: 80%;
  height: 18px;
}

.skeleton-price {
  width: 40%;
}

.skeleton-info {
  width: 65%;
}

.skeleton-actions {
  width: 100%;
  height: 32px;
  margin-top: 16px;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}

.image-container {
  position: relative;
  width: 100%;
  padding-bottom: 100%;
  overflow: hidden;
  background: #f8f9fa;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: transform 0.4s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.product-badges {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  gap: 6px;
  z-index: 1;
}

.badge {
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  color: white;
}

.badge.new {
  background: rgba(103, 194, 58, 0.9);
}

.badge.discount {
  background: rgba(255, 71, 87, 0.9);
}

.product-content {
  padding: 12px;
}

.product-name {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: #2c3e50;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  height: 40px;
}

.product-info {
  margin-top: 8px;
}

.current-price {
  display: flex;
  align-items: baseline;
  gap: 4px;
}

.current-price .symbol {
  font-size: 12px;
  color: #ff4757;
}

.current-price .amount {
  font-size: 18px;
  font-weight: 600;
  color: #ff4757;
}

.original-price {
  color: #909399;
  font-size: 12px;
  text-decoration: line-through;
}

.stock-info {
  display: flex;
  justify-content: space-between;
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.stock {
  color: #67C23A;
}

.stock.low {
  color: #E6A23C;
}

.product-footer {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #f0f2f5;
}

.action-group {
  display: flex;
  gap: 8px;
}

.favorite-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  color: #909399;
  transition: all 0.3s ease;
  background: #f5f7fa;
}

.favorite-btn:hover {
  background: #f0f2f5;
}

.favorite-btn.is-favorite {
  color: #ff4757;
  background: rgba(255, 71, 87, 0.1);
}

.add-to-cart {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: #67C23A;
  color: white;
  padding: 8px;
  border-radius: 6px;
  font-size: 13px;
  transition: all 0.3s ease;
}

.add-to-cart:hover {
  background: #85ce61;
}

.add-to-cart i {
  font-size: 14px;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  color: #909399;
  font-size: 20px;
  background: #f5f7fa;
}
</style> 