<template>
  <div class="products-page">
    <front-header></front-header>
    <div class="main-content">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2 class="page-title">
          <i class="el-icon-shopping-bag-1"></i>
          <span>全部商品</span>
        </h2>
        <div class="page-subtitle">精选优质农产品，品质保障</div>
      </div>

      <!-- 顶部过滤器 -->
      <div class="filter-bar">
        <div class="filter-group">
          <el-dropdown trigger="click" @command="handleCategoryChange">
            <span class="filter-label">
              <i class="el-icon-menu"></i>
              分类
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="">全部商品</el-dropdown-item>
              <el-dropdown-item 
                v-for="category in categories" 
                :key="category.id"
                :command="category.id"
              >{{ category.name }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <el-dropdown trigger="click" @command="handlePriceRangeChange">
            <span class="filter-label">
              <i class="el-icon-price-tag"></i>
              价格
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="">全部价格</el-dropdown-item>
              <el-dropdown-item 
                v-for="(range, index) in priceRanges" 
                :key="index"
                :command="range.value"
              >{{ range.label }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <el-dropdown trigger="click" @command="handleSortChange">
            <span class="filter-label">
              <i class="el-icon-sort"></i>
              排序
              <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item 
                v-for="option in sortOptions" 
                :key="option.value"
                :command="option.value"
              >{{ option.label }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>

        <div class="search-box">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索商品" 
            prefix-icon="el-icon-search"
            clearable
            @clear="handleSearch"
            @keyup.enter.native="handleSearch"
          >
          </el-input>
        </div>
      </div>

      <!-- 已选择的过滤条件 -->
      <div class="selected-filters" v-if="hasFilters">
        <el-tag 
          v-if="selectedCategory" 
          closable
          @close="handleCategoryChange('')"
        >
          分类: {{ getCategoryName(selectedCategory) }}
        </el-tag>
        <el-tag 
          v-if="priceRange" 
          closable
          @close="handlePriceRangeChange('')"
        >
          价格: {{ getPriceRangeLabel(priceRange) }}
        </el-tag>
        <el-tag 
          v-if="sortBy !== 'default'" 
          closable
          @close="handleSortChange('default')"
        >
          {{ getSortLabel(sortBy) }}
        </el-tag>
        <el-tag 
          v-if="searchKeyword" 
          closable
          @close="clearSearch"
        >
          关键词: {{ searchKeyword }}
        </el-tag>
      </div>

      <!-- 商品列表 -->
      <div class="products-grid" v-loading="loading">
        <transition-group name="fade-list" tag="div" class="grid-container">
          <div 
            v-for="product in products" 
            :key="product.id"
            class="product-item"
          >
            <product-card :product="product" />
          </div>
        </transition-group>
        
        <div v-if="!loading && products.length === 0" class="empty-state">
          <i class="el-icon-goods"></i>
          <p>暂无相关商品</p>
          <el-button type="text" @click="resetFilters">清除筛选条件</el-button>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          background
          :current-page.sync="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[12, 24, 36, 48]"
          layout="sizes, prev, pager, next, jumper, total"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        >
        </el-pagination>
      </div>
    </div>
    <front-footer></front-footer>
  </div>
</template>

<script>
import FrontHeader from '@/components/front/FrontHeader.vue'
import FrontFooter from '@/components/front/FrontFooter.vue'
import ProductCard from '@/components/front/ProductCard.vue'
import Request from '@/utils/request'
import { debounce } from 'lodash'

export default {
  name: 'Products',
  components: {
    FrontHeader,
    FrontFooter,
    ProductCard
  },
  data() {
    return {
      loading: false,
      products: [],
      categories: [],
      selectedCategory: '',
      priceRange: '',
      priceRanges: [
        { label: '0-50元', value: '0-50' },
        { label: '50-100元', value: '50-100' },
        { label: '100-200元', value: '100-200' },
        { label: '200元以上', value: '200-' }
      ],
      sortOptions: [
        { label: '默认排序', value: 'default' },
        { label: '销量优先', value: 'sales,desc' },
        { label: '价格从低到高', value: 'price,asc' },
        { label: '价格从高到低', value: 'price,desc' }
      ],
      sortBy: 'default',
      searchKeyword: '',
      currentPage: 1,
      pageSize: 12,
      total: 0,
      debouncedSearch: null
    }
  },
  computed: {
    hasFilters() {
      return this.selectedCategory || this.priceRange || this.sortBy !== 'default' || this.searchKeyword
    }
  },
  methods: {
    // 获取商品分类
    async getCategories() {
      try {
        const res = await Request.get('/category/all')
        if (res.code === '0') {
          this.categories = res.data
        }
      } catch (error) {
        console.error('获取分类失败:', error)
      }
    },
    // 获取商品列表
    async getProducts() {
      this.loading = true
      try {
        const params = {
          status: 1,
          currentPage: this.currentPage,
          size: this.pageSize
        }

        // 添加分类筛选
        if (this.selectedCategory) {
          params.categoryId = this.selectedCategory
        }

        // 添加价格区间筛选
        if (this.priceRange) {
          const [min, max] = this.priceRange.split('-')
          if (min) params.minPrice = min
          if (max) params.maxPrice = max
        }

        // 添加排序
        if (this.sortBy !== 'default') {
          const [field, order] = this.sortBy.split(',')
          params.sortField = field
          params.sortOrder = order
        }

        // 添加搜索关键词
        if (this.searchKeyword) {
          params.name = this.searchKeyword
        }

        const res = await Request.get('/product/page', { params })
        if (res.code === '0') {
          if (res.data && res.data.records) {
            this.products = res.data.records.map(product => ({
              ...product,
              isFavorite: false,
              imageUrl: product.imageUrl?.startsWith('http') ? product.imageUrl : `${product.imageUrl}`
            }))
            this.total = res.data.total
          } else {
            this.products = []
            this.total = 0
          }
        } else {
          this.products = []
          this.total = 0
        }
      } catch (error) {
        console.error('获取商品列表失败:', error)
        this.$message.error('获取商品列表失败')
        this.products = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },
    handleCategoryChange(categoryId) {
      this.selectedCategory = categoryId
      this.currentPage = 1
      this.getProducts()
    },
    handlePriceRangeChange(range) {
      this.priceRange = range
      this.currentPage = 1
      this.getProducts()
    },
    handleSortChange(value) {
      this.sortBy = value
      this.currentPage = 1
      this.getProducts()
    },
    handleSearch() {
      this.debouncedSearch()
    },
    handlePageChange(page) {
      this.currentPage = page
      this.getProducts()
    },
    handleRouteChange() {
      const query = {}
      if (this.selectedCategory) {
        query.category = this.selectedCategory
      }
      if (this.searchKeyword) {
        query.keyword = this.searchKeyword
      }
      // 更新URL，但不触发路由变化
      this.$router.replace({ query }).catch(() => {})
    },
    getCategoryName(id) {
      const category = this.categories.find(c => c.id === id)
      return category ? category.name : '全部'
    },
    getPriceRangeLabel(value) {
      const range = this.priceRanges.find(r => r.value === value)
      return range ? range.label : '全部'
    },
    getSortLabel(value) {
      const option = this.sortOptions.find(o => o.value === value)
      return option ? option.label : '默认排序'
    },
    clearSearch() {
      this.searchKeyword = ''
      this.handleSearch()
    },
    resetFilters() {
      this.selectedCategory = ''
      this.priceRange = ''
      this.sortBy = 'default'
      this.searchKeyword = ''
      this.currentPage = 1
      this.getProducts()
    },
    handleSizeChange(size) {
      this.pageSize = size
      this.currentPage = 1
      this.getProducts()
    }
  },
  watch: {
    searchKeyword() {
      this.handleSearch()
      this.handleRouteChange()
    },
    selectedCategory() {
      this.handleRouteChange()
    },
    sortBy() {
      this.currentPage = 1
      this.getProducts()
    }
  },
  created() {
    this.debouncedSearch = debounce(() => {
      this.currentPage = 1
      this.getProducts()
    }, 300)

    this.getCategories()
    this.getProducts()
    
    const { category, keyword } = this.$route.query
    if (category) this.selectedCategory = category
    if (keyword) {
      this.searchKeyword = keyword
      this.handleSearch()
    }
  },
  beforeDestroy() {
    if (this.debouncedSearch) {
      this.debouncedSearch.cancel()
    }
  }
}
</script>

<style scoped>
.products-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  //background-color: #f8f9fa;
  background: linear-gradient(to bottom, #fff, #bade62);
}

.main-content {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  width: 100%;
  box-sizing: border-box;
}

/* 页面标题样式 */
.page-header {
  margin-bottom: 24px;
  background: white;
  padding: 20px 24px;
  border-radius: 12px;
  border: 1px solid #ebeef5;
  position: relative;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.page-header::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(to bottom, #67C23A, #85ce61);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
  font-size: 24px;
  font-weight: 500;
  color: #2c3e50;
}

.page-title i {
  font-size: 28px;
  color: #67C23A;
}

.page-subtitle {
  margin-top: 4px;
  font-size: 14px;
  color: #909399;
  padding-left: 36px;
}

/* 过滤器样式 */
.filter-bar {
  background: white;
  border-radius: 12px;
  padding: 16px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border: 1px solid #ebeef5;
}

.filter-group {
  display: flex;
  gap: 24px;
  position: relative;
}

.filter-label {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 6px;
  transition: all 0.3s ease;
  color: #606266;
  font-size: 14px;
  position: relative;
}

.filter-label:hover {
  background: rgba(103, 194, 58, 0.05);
  color: #67C23A;
}

.filter-label::after {
  content: '';
  position: absolute;
  right: -12px;
  top: 50%;
  transform: translateY(-50%);
  width: 1px;
  height: 16px;
  background: #ebeef5;
}

.filter-group > :last-child .filter-label::after {
  display: none;
}

.search-box {
  width: 280px;
}

.search-box :deep(.el-input__inner) {
  border-radius: 6px;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.search-box :deep(.el-input__inner:hover) {
  border-color: #c0c4cc;
}

.search-box :deep(.el-input__inner:focus) {
  border-color: #67C23A;
}

/* 已选择的过滤条件 */
.selected-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 20px;
  padding: 16px 24px;
  background: #f8faf5;
  border-radius: 12px;
}

.selected-filters :deep(.el-tag) {
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
  border-radius: 14px;
  background: white;
  border: 1px solid #e4e7ed;
  color: #606266;
}

.selected-filters :deep(.el-tag .el-tag__close) {
  background-color: transparent;
  color: #909399;
  font-weight: bold;
}

.selected-filters :deep(.el-tag .el-tag__close:hover) {
  background-color: #909399;
  color: white;
}

/* 下拉菜单样式优化 */
:deep(.el-dropdown-menu) {
  padding: 6px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-dropdown-menu__item) {
  padding: 8px 16px;
  font-size: 14px;
  border-radius: 4px;
  margin: 2px 0;
  color: #606266;
}

:deep(.el-dropdown-menu__item:hover) {
  background-color: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}

:deep(.el-dropdown-menu__item.is-disabled) {
  color: #c0c4cc;
  cursor: not-allowed;
  background-color: transparent;
}

/* 商品网格 */
.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  padding: 4px;
}

.product-item {
  transition: all 0.3s ease;
}

.product-item:hover {
  transform: translateY(-4px);
}

/* 动画效果 */
.fade-list-enter-active, .fade-list-leave-active {
  transition: all 0.3s ease;
}

.fade-list-enter, .fade-list-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: #909399;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  margin: 0 0 16px;
}

/* 分页 */
.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

:deep(.el-pagination.is-background .el-pager li:not(.disabled).active) {
  background-color: #67C23A;
}

/* 响应式布局优化 */
@media (max-width: 768px) {
  .page-header {
    margin-bottom: 20px;
  }

  .page-title {
    font-size: 20px;
  }

  .page-title i {
    font-size: 24px;
  }

  .page-subtitle {
    padding-left: 36px;
    font-size: 13px;
  }

  .filter-bar {
    flex-direction: column;
    gap: 16px;
    padding: 16px;
  }

  .search-box {
    width: 100%;
  }

  .filter-group {
    width: 100%;
    justify-content: space-between;
    gap: 8px;
  }

  .filter-label::after {
    display: none;
  }

  .selected-filters {
    padding: 12px 16px;
  }

  .grid-container {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }
}
</style> 