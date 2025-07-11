<template>
  <div class="notice-container">
    <div class="notice-header">
      <div class="title-wrapper">
        <i class="el-icon-bell"></i>
        <span>最新公告</span>
      </div>
      <el-button type="text" class="more-btn" @click="showAllNotices">
        查看更多
        <i class="el-icon-arrow-right"></i>
      </el-button>
    </div>
    
    <el-carousel 
      :interval="4000" 
      direction="vertical" 
      :height="carouselHeight"
      :autoplay="true"
      indicator-position="right"
      class="notice-carousel"
    >
      <el-carousel-item v-for="notice in notices" :key="notice.id">
        <div class="notice-item" @click="showNoticeDetail(notice)">
          <div class="notice-content">
            <div class="notice-meta">
              <div class="notice-tags">
                <el-tag 
                  v-for="tag in getTags(notice.tags)" 
                  :key="tag"
                  size="mini"
                  :type="getTagType(tag)"
                  effect="dark"
                >{{ tag }}</el-tag>
              </div>
              <span class="notice-time">{{ formatTime(notice.time) }}</span>
            </div>
            <div class="notice-title">{{ notice.title }}</div>
            <div class="notice-desc">{{ notice.content }}</div>
          </div>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 所有公告列表对话框 -->
    <el-dialog
      title="全部公告"
      :visible.sync="dialogVisible"
      width="800px"
      custom-class="notice-list-dialog"
    >
      <div class="notice-list" v-loading="dialogLoading">
        <div 
          v-for="notice in allNotices" 
          :key="notice.id"
          class="notice-list-item"
          @click="showNoticeDetail(notice)"
        >
          <div class="notice-list-content">
            <div class="notice-list-meta">
              <div class="notice-list-tags">
                <el-tag 
                  v-for="tag in getTags(notice.tags)" 
                  :key="tag"
                  size="mini"
                  :type="getTagType(tag)"
                >{{ tag }}</el-tag>
              </div>
              <span class="notice-list-time">{{ formatTime(notice.time, true) }}</span>
            </div>
            <div class="notice-list-title">{{ notice.title }}</div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-pagination
          :current-page.sync="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="handlePageChange"
        />
      </div>
    </el-dialog>

    <!-- 公告详情对话框 -->
    <el-dialog
      :visible.sync="detailVisible"
      width="700px"
      custom-class="notice-detail-dialog"
      :title="currentNotice.title"
    >
      <div class="notice-detail-header">
        <div class="notice-detail-meta">
          <div class="notice-detail-tags">
            <el-tag 
              v-for="tag in getTags(currentNotice.tags)" 
              :key="tag"
              size="small"
              :type="getTagType(tag)"
            >{{ tag }}</el-tag>
          </div>
          <span class="notice-detail-time">发布时间：{{ formatTime(currentNotice.time, true) }}</span>
        </div>
      </div>
      <div class="notice-detail-content" v-html="currentNotice.content"></div>
    </el-dialog>
  </div>
</template>

<script>
import Request from '@/utils/request'
import { formatTime } from '@/utils/time'

export default {
  name: 'FrontNotice',
  data() {
    return {
      notices: [],
      loading: false,
      dialogVisible: false,
      dialogLoading: false,
      allNotices: [],
      currentPage: 1,
      pageSize: 10,
      total: 0,
      detailVisible: false,
      currentNotice: {},
      carouselHeight: '320px'
    }
  },
  created() {
    this.fetchNotices()
  },
  methods: {
    async fetchNotices() {
      this.loading = true
      try {
        const res = await Request.get('/notice/page', {
          params: {
            currentPage: 1,
            size: 5
          }
        })
        if (res.code === '0') {
          this.notices = res.data.records
        }
      } catch (error) {
        console.error('获取通知失败:', error)
      }
      this.loading = false
    },
    formatTime(time, showYear = false) {
      if (!time) return ''
      const date = new Date(time)
      if (showYear) {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${month}-${day}`
    },
    getTags(tags) {
      if (!tags) return []
      return typeof tags === 'string' ? tags.split(',') : []
    },
    getTagType(tag) {
      const typeMap = {
        '重要': 'danger',
        '紧急': 'warning',
        '普通': 'info',
        '公告': 'success'
      }
      return typeMap[tag] || 'info'
    },
    showNoticeDetail(notice) {
      this.currentNotice = notice
      this.detailVisible = true
    },
    async showAllNotices() {
      this.dialogVisible = true
      this.currentPage = 1
      await this.fetchAllNotices()
    },
    async fetchAllNotices() {
      this.dialogLoading = true
      try {
        const res = await Request.get('/notice/page', {
          params: {
            currentPage: this.currentPage,
            size: this.pageSize
          }
        })
        if (res.code === '0') {
          this.allNotices = res.data.records
          this.total = res.data.total
        }
      } catch (error) {
        console.error('获取所有通知失败:', error)
      }
      this.dialogLoading = false
    },
    async handlePageChange(page) {
      this.currentPage = page
      await this.fetchAllNotices()
    }
  }
}
</script>

<style scoped>
.notice-container {
  background: white;
  border-radius: 8px;
  padding: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 0 10px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #2c3e50;
}

.title-wrapper i {
  color: #67C23A;
  font-size: 18px;
}

.notice-carousel {
  flex: 1;
  background: #f8faf5;
  border-radius: 8px;
  overflow: hidden;
  height: calc(100% - 50px);
}

.notice-item {
  height: 100%;
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.notice-item:hover {
  background: rgba(103, 194, 58, 0.05);
}

.notice-content {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.notice-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.notice-tags {
  display: flex;
  gap: 6px;
}

.notice-time {
  font-size: 12px;
  color: #909399;
}

.notice-title {
  font-size: 16px;
  font-weight: 500;
  color: #2c3e50;
  margin-bottom: 12px;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-desc {
  font-size: 13px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  line-height: 1.6;
  opacity: 0.8;
  flex: 1;
}

.notice-footer {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.read-more {
  color: #67C23A;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.read-more i {
  transition: transform 0.3s ease;
}

.read-more:hover i {
  transform: translateX(4px);
}

/* 公告详情对话框样式 */
:deep(.notice-detail-dialog) {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.notice-detail-dialog .el-dialog__header) {
  padding: 20px;
  background: linear-gradient(135deg, #f8faf5, #fff);
  border-bottom: 1px solid #ebeef5;
  margin: 0;
}

:deep(.notice-detail-dialog .el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
  line-height: 1.4;
}

:deep(.notice-detail-dialog .el-dialog__headerbtn) {
  top: 20px;
  right: 20px;
}

:deep(.notice-detail-dialog .el-dialog__headerbtn:hover .el-icon-close) {
  color: #67C23A;
}

:deep(.notice-detail-dialog .el-dialog__body) {
  padding: 0;
}

.notice-detail-header {
  padding: 16px 24px;
  background: #f8faf5;
}

.notice-detail-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.notice-detail-tags {
  display: flex;
  gap: 8px;
}

.notice-detail-time {
  font-size: 14px;
  color: #909399;
}

.notice-detail-content {
  padding: 24px;
  font-size: 14px;
  line-height: 1.8;
  color: #606266;
  min-height: 200px;
  max-height: 500px;
  overflow-y: auto;
}

.notice-detail-content :deep(p) {
  margin: 0 0 16px;
}

.notice-detail-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 16px 0;
}

.notice-detail-content :deep(a) {
  color: #67C23A;
  text-decoration: none;
  transition: all 0.3s ease;
}

.notice-detail-content :deep(a:hover) {
  color: #85ce61;
  text-decoration: underline;
}

/* 自定义滚动条 */
.notice-detail-content::-webkit-scrollbar {
  width: 6px;
}

.notice-detail-content::-webkit-scrollbar-thumb {
  background: rgba(144, 147, 153, 0.3);
  border-radius: 3px;
}

.notice-detail-content::-webkit-scrollbar-track {
  background: transparent;
}

/* 公告列表对话框样式 */
:deep(.notice-list-dialog) {
  border-radius: 12px;
}

:deep(.notice-list-dialog .el-dialog__header) {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  margin: 0;
}

:deep(.notice-list-dialog .el-dialog__body) {
  padding: 20px;
  max-height: 600px;
  overflow-y: auto;
}

.notice-list-item {
  padding: 16px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.3s ease;
}

.notice-list-item:last-child {
  border-bottom: none;
}

.notice-list-item:hover {
  background: #f8faf5;
}

.notice-list-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.notice-list-tags {
  display: flex;
  gap: 6px;
}

.notice-list-time {
  font-size: 13px;
  color: #909399;
}

.notice-list-title {
  font-size: 15px;
  color: #2c3e50;
  line-height: 1.4;
}

:deep(.notice-list-dialog .el-dialog__footer) {
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}

:deep(.el-pagination) {
  justify-content: center;
}

.more-btn {
  color: #67C23A;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s ease;
}

.more-btn:hover {
  transform: translateX(4px);
}

.more-btn i {
  transition: transform 0.3s ease;
}

.more-btn:hover i {
  transform: translateX(2px);
}

:deep(.el-carousel__indicators--vertical) {
  right: 10px;
}

:deep(.el-carousel__indicator--vertical) {
  padding: 3px 0;
}

:deep(.el-carousel__indicator--vertical .el-carousel__button) {
  width: 3px;
  height: 16px;
  background-color: rgba(103, 194, 58, 0.2);
}

:deep(.el-carousel__indicator--vertical.is-active .el-carousel__button) {
  background-color: #67C23A;
}
</style> 