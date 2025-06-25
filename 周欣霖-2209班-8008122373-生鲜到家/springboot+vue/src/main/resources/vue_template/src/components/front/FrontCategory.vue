<template>
  <div class="category-container">
    <div class="category-wrapper">
      <div 
        v-for="(category, index) in visibleCategories" 
        :key="index" 
        class="category-item"
        @click="handleCategoryClick(category)"
      >
        <div class="icon-wrapper">
          <i :class="['icon', category.icon]"></i>
          <div class="icon-bg"></div>
        </div>
        <span class="category-name">{{ category.name }}</span>
        <div class="hover-indicator"></div>
      </div>
      <div 
        v-if="showMore" 
        class="category-item more-item"
        @click="handleMoreClick"
      >
        <div class="icon-wrapper">
          <i class="el-icon-more"></i>
          <div class="icon-bg"></div>
        </div>
        <span class="category-name">更多分类</span>
        <div class="hover-indicator"></div>
      </div>
    </div>
  </div>
</template>

<script>
import Request from '@/utils/request.js'

export default {
  name: 'FrontCategory',
  data() {
    return {
      categories: [],
      maxVisible: 6
    }
  },
  computed: {
    visibleCategories() {
      return this.categories.slice(0, this.maxVisible)
    },
    showMore() {
      return this.categories.length > this.maxVisible
    }
  },
  created() {
    this.fetchCategories()
  },
  methods: {
    async fetchCategories() {
      try {
        const res = await Request.get('/category/all')
        if (res.code === '0') {
          this.categories = res.data.map(item => ({
            ...item,
            icon: item.icon
          }))
        }
      } catch (error) {
        console.error('获取分类数据失败:', error)
      }
    },
    getCategoryIcon(name) {
      const iconMap = {
        '蔬菜': 'el-icon-food',
        '水果': 'el-icon-apple',
        '谷物': 'el-icon-dish',
        '干货': 'el-icon-dessert',
        '肉类': 'el-icon-food',
        '水产': 'el-icon-dish-1',
        '农副产品': 'el-icon-sugar',
        '其他': 'el-icon-more'
      }
      return iconMap[name] || 'el-icon-more'
    },
    handleCategoryClick(category) {
      this.$router.push({
        name: 'category',
        params: { id: category.id },
        query: { name: category.name }
      })
    },
    handleMoreClick() {
      this.$router.push('/products')
    }
  }
}
</script>

<style scoped>
.category-container {
  background: linear-gradient(to right, #ffffff, #f8faf5);
  padding: 16px 0;
  margin: 16px 0;
  border-radius: 16px;
  position: relative;
  overflow: hidden;
}

.category-container::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image: 
    radial-gradient(circle at 0% 0%, rgba(103, 194, 58, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 100% 0%, rgba(103, 194, 58, 0.03) 0%, transparent 50%);
  opacity: 0.8;
  z-index: 0;
}

.category-wrapper {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.category-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.category-item:hover {
  background: rgba(255, 255, 255, 0.8);
  transform: translateY(-2px);
}

.icon-wrapper {
  width: 52px;
  height: 52px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.icon {
  font-size: 24px;
  color: #67C23A;
  position: relative;
  z-index: 2;
  transition: all 0.3s ease;
}

.icon-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, 
    rgba(103, 194, 58, 0.1),
    rgba(103, 194, 58, 0.15)
  );
  border-radius: 12px;
  transition: all 0.3s ease;
}

.category-item:hover .icon-bg {
  transform: scale(1.1) rotate(-5deg);
  background: linear-gradient(135deg, 
    rgba(103, 194, 58, 0.15),
    rgba(103, 194, 58, 0.2)
  );
}

.category-item:hover .icon {
  transform: scale(1.1);
}

.category-name {
  font-size: 14px;
  color: #2c3e50;
  font-weight: 500;
  transition: all 0.3s ease;
  position: relative;
}

.hover-indicator {
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: #67C23A;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.category-item:hover .hover-indicator {
  width: 20px;
}

.more-item .icon-bg {
  background: linear-gradient(135deg, 
    rgba(144, 147, 153, 0.1),
    rgba(144, 147, 153, 0.15)
  );
}

.more-item .icon {
  color: #909399;
}

.more-item:hover .icon-bg {
  background: linear-gradient(135deg, 
    rgba(144, 147, 153, 0.15),
    rgba(144, 147, 153, 0.2)
  );
}

.more-item:hover .hover-indicator {
  background: #909399;
}

@media (max-width: 768px) {
  .category-wrapper {
    flex-wrap: wrap;
  }
  
  .category-item {
    flex: 0 0 calc(25% - 9px);
  }
  
  .icon-wrapper {
    width: 44px;
    height: 44px;
  }
  
  .icon {
    font-size: 20px;
  }
  
  .category-name {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .category-item {
    flex: 0 0 calc(33.33% - 8px);
  }
  
  .icon-wrapper {
    width: 40px;
    height: 40px;
  }
  
  .icon {
    font-size: 18px;
  }
}
</style> 