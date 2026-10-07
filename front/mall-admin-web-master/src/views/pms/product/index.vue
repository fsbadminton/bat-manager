<template> 
  <div class="app-container">
    <el-card class="filter-container" shadow="never">
      <div>
        <i class="el-icon-search"></i>
        <span>筛选搜索</span>
        <el-button
          style="float: right"
          @click="handleSearchList()"
          type="primary"
          size="small">
          查询结果
        </el-button>
        <el-button
          style="float: right;margin-right: 15px"
          @click="handleResetSearch()"
          size="small">
          重置
        </el-button>
      </div>
      <div style="margin-top: 15px">
        <el-form :inline="true" :model="listQuery" size="small" label-width="140px">
          <el-form-item label="输入搜索：">
            <el-input style="width: 203px" v-model="listQuery.keyword" placeholder="球拍名称"></el-input>
          </el-form-item>
          <el-form-item label="球拍货号：">
            <el-input style="width: 203px" v-model="listQuery.productSn" placeholder="球拍货号"></el-input>
          </el-form-item>
          <el-form-item label="球拍分类：">
            <el-select 
              v-model="listQuery.categoryId" 
              placeholder="请选择分类" 
              clearable
              style="width: auto; min-width: 120px;">
              <el-option
                v-for="item in productCateOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="球拍品牌：">
            <el-select v-model="listQuery.brandId" placeholder="请选择品牌" clearable>
              <el-option
                v-for="item in brandOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="上架状态：">
            <el-select v-model="listQuery.publishStatus" placeholder="全部" clearable>
              <el-option
                v-for="item in publishStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="审核状态：">
            <el-select v-model="listQuery.auditStatus" placeholder="全部" clearable>
              <el-option
                v-for="item in auditStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    <el-card class="operate-container" shadow="never">
      <i class="el-icon-tickets"></i>
      <span>数据列表</span>
      <el-button
        class="btn-add"
        @click="handleAddProduct()"
        size="mini">
        添加
      </el-button>
    </el-card>
    <div class="table-container">
      <el-table ref="productTable"
                :data="list"
                style="width: 100%"
                @selection-change="handleSelectionChange"
                v-loading="listLoading"
                border>
        <el-table-column type="selection" width="60" align="center"></el-table-column>
        <el-table-column label="编号" width="100" align="center">
          <template slot-scope="scope">{{scope.row.productId}}</template>
        </el-table-column>
        <el-table-column label="球拍图片" width="120" align="center">
          <template slot-scope="scope">
            <img
              style="width: 100%; height: 100%; object-fit: cover; border-radius: 4px; cursor: pointer;"
              :src="scope.row.imageUrl"
              @click="previewImage(scope.row)"
              alt="球拍图片"
            />
          </template>
        </el-table-column>
        <el-table-column label="球拍名称" align="center">
          <template slot-scope="scope">
            <p>{{scope.row.name}}</p>
            <p>品牌：{{scope.row.brandName}}</p>
          </template>
        </el-table-column>
        <el-table-column label="价格/货号" width="120" align="center">
          <template slot-scope="scope">
            <p>价格：￥{{scope.row.price}}</p>
            <p>货号：{{scope.row.productSn}}</p>
          </template>
        </el-table-column>
        <el-table-column label="标签" width="140" align="center">
          <template slot-scope="scope">
            <p>上架：
              <el-switch
                @change="handlePublishStatusChange(scope.$index, scope.row)"
                :active-value="1"
                :inactive-value="0"
                v-model="scope.row.publishStatus">
              </el-switch>
            </p>
            <p>新品：
              <el-switch
                @change="handleNewStatusChange(scope.$index, scope.row)"
                :active-value="1"
                :inactive-value="0"
                v-model="scope.row.newStatus">
              </el-switch>
            </p>
            <p>推荐：
              <el-switch
                @change="handleRecommendStatusChange(scope.$index, scope.row)"
                :active-value="1"
                :inactive-value="0"
                v-model="scope.row.recommandStatus">
              </el-switch>
            </p>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="100" align="center">
          <template slot-scope="scope">{{scope.row.stock}}</template>
        </el-table-column>
        <el-table-column label="销量" width="100" align="center">
          <template slot-scope="scope">{{scope.row.sale}}</template>
        </el-table-column>
        <el-table-column label="审核状态" width="100" align="center">
          <template slot-scope="scope">
            <p>{{scope.row.auditStatus | auditStatusFilter}}</p>
            <p>
              <el-button
                type="text"
                @click="handleShowVerifyDetail(scope.$index, scope.row)">审核详情
              </el-button>
            </p>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center">
          <template slot-scope="scope">
            <p>
              <el-button
                size="mini"
                @click="handleShowProduct(scope.$index, scope.row)">查看
              </el-button>
              <el-button
                size="mini"
                @click="handleUpdateProduct(scope.$index, scope.row)">编辑
              </el-button>
            </p>
            <p>
              <el-button
                size="mini"
                @click="handleShowLog(scope.$index, scope.row)">日志
              </el-button>
              <el-button
                size="mini"
                type="danger"
                @click="handleDelete(scope.$index, scope.row)">删除
              </el-button>
            </p>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="batch-operate-container">
      <el-select
        size="small"
        v-model="operateType" placeholder="批量操作">
        <el-option
          v-for="item in operates"
          :key="item.value"
          :label="item.label"
          :value="item.value">
        </el-option>
      </el-select>
      <el-button
        style="margin-left: 20px"
        class="search-button"
        @click="handleBatchOperate()"
        type="primary"
        size="small">
        确定
      </el-button>
    </div>
    <div class="pagination-container">
      <el-pagination
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        layout="total, sizes,prev, pager, next,jumper"
        :page-size="listQuery.pageSize"
        :page-sizes="[5,10,15]"
        :current-page.sync="listQuery.pageNum"
        :total="total">
      </el-pagination>
    </div>
    <!-- 图片预览弹窗 -->
    <el-dialog :visible.sync="previewVisible" width="60%" :show-close="true">
      <div style="text-align: center;">
        <el-button v-if="previewList.length>1" @click="prevPreview" size="mini">上一张</el-button>
        <img v-if="previewList.length>0" :src="previewList[previewIndex]" style="max-width: 100%; max-height: 60vh; margin: 0 10px;" />
        <el-button v-if="previewList.length>1" @click="nextPreview" size="mini">下一张</el-button>
      </div>
      <div slot="footer" class="dialog-footer">
        <span style="float:left;color:#666;">{{ previewIndex+1 }} / {{ previewList.length }}</span>
        <el-button @click="previewVisible = false">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    fetchList,
    fetchPageList,
    updateDeleteStatus,
    updateNewStatus,
    updateRecommendStatus,
    updatePublishStatus,
    updateStatus,
    updateProduct,
    deleteProduct
  } from '@/api/product'

  import {fetchBrandList as fetchBrandList} from '@/api/brand'
  import {fetchListWithChildren, fetchListWithChildrenAdmin, fetchListAll, fetchCategoryList} from '@/api/productCate'

  const defaultListQuery = {
    keyword: null,
    pageNum: 1,
    pageSize: 5,
    publishStatus: null,
    auditStatus: null,
    productSn: null,
    productCategoryId: null,
    brandId: null
  };
  export default {
    name: "productList",
    data() {
      return {
        operates: [
          {
            label: "球拍上架",
            value: "publishOn"
          },
          {
            label: "球拍下架",
            value: "publishOff"
          },
          {
            label: "设为推荐",
            value: "recommendOn"
          },
          {
            label: "取消推荐",
            value: "recommendOff"
          },
          {
            label: "设为新品",
            value: "newOn"
          },
          {
            label: "取消新品",
            value: "newOff"
          },
          {
            label: "转移到分类",
            value: "transferCategory"
          },
          {
            label: "移入回收站",
            value: "recycle"
          }
        ],
        operateType: null,
        listQuery: Object.assign({}, defaultListQuery),
        list: null,
        total: null,
        listLoading: true,
        multipleSelection: [],
        productCateOptions: [],
        brandOptions: [],
        publishStatusOptions: [{
          value: 1,
          label: '上架'
        }, {
          value: 0,
          label: '下架'
        }],
        auditStatusOptions: [{
          value: 0,
          label: '未审核'
        }, {
          value: 1,
          label: '审核通过'
        }]
        ,
        // 图片预览状态
        previewVisible: false,
        previewList: [],
        previewIndex: 0
      }
    },
    created() {
      this.getList();
      this.getBrandList();
      this.getProductCateList();
    },
    filters: {
      auditStatusFilter(value) {
        if (value === 1) {
          return '审核通过';
        } else if (value === 0) {
          return '未审核';
        } else {
          return '未知';
        }
      }
    },
    methods: {
      
      getList() {
        this.listLoading = true;
        
        // 构建查询参数，过滤掉 null 和空字符串
        // 注意：后端期望的参数名可能不同，需要映射
        const queryParams = {};
        Object.keys(this.listQuery).forEach(key => {
          const value = this.listQuery[key];
          // 保留 pageNum 和 pageSize，过滤掉 null、undefined 和空字符串
          if (key === 'pageNum' || key === 'pageSize') {
            queryParams[key] = value;
          } else if (value !== null && value !== undefined && value !== '') {
            // 参数名映射
            if (key === 'keyword') {
              // 前端使用 keyword，后端期望 name
              queryParams['name'] = value;
            } else if (key === 'productCategoryId') {
              // 前端使用 productCategoryId，后端期望 category
              queryParams['category'] = value;
            } else if (key === 'brandId') {
              // 前端使用 brandId，后端期望 brandName
              // 注意：后端期望的是品牌名称（brandName），但我们只有品牌ID
              // 需要从 brandOptions 中查找品牌名称
              const brand = this.brandOptions.find(b => b.value === value);
              if (brand && brand.label) {
                queryParams['brandName'] = brand.label;
              } else {
                // 如果找不到品牌名称，仍然发送ID（后端可能也支持）
                queryParams['brandName'] = value;
              }
            } else if (key === 'auditStatus') {
              // 前端和后端都使用 auditStatus
              queryParams['auditStatus'] = value;
            } else {
              queryParams[key] = value;
            }
          }
        });
        
        // 调试：打印查询参数和完整的 listQuery
        console.log('=== 查询参数调试 ===');
        console.log('完整的 listQuery:', JSON.stringify(this.listQuery, null, 2));
        console.log('过滤后的查询参数（已映射）:', JSON.stringify(queryParams, null, 2));
        console.log('productCategoryId 值:', this.listQuery.productCategoryId);
        console.log('brandId 值:', this.listQuery.brandId);
        console.log('auditStatus 值:', this.listQuery.auditStatus);
        console.log('映射后的 category 参数:', queryParams.category);
        console.log('映射后的 brandName 参数:', queryParams.brandName);
        console.log('映射后的 auditStatus 参数:', queryParams.auditStatus);
        
        // 使用 fetchPageList 调用 /admin/product/page 接口
        fetchPageList(queryParams).then(response => {
          this.listLoading = false;
          // 调试：打印完整响应数据
          console.log('产品API响应数据:', response);
          console.log('response.data:', response.data);
          
          // 后端返回 Result<PageResult>，经过拦截器处理后，response 就是整个响应对象
          // response.data 就是 PageResult 对象
          if (response && response.data) {
            // PageResult 格式：{ records: [...], total: 100, ... }
            const pageResult = response.data;
            this.list = pageResult.records || pageResult.list || pageResult.items || [];
            this.total = pageResult.total || pageResult.totalCount || pageResult.totalElements || 0;
          } else if (Array.isArray(response)) {
            // 如果直接返回数组（不应该发生，但做兼容处理）
            this.list = response;
            this.total = response.length;
          } else {
            // 如果数据在顶层（不应该发生，但做兼容处理）
            this.list = response.records || response.list || response.items || [];
            this.total = response.total || response.totalCount || response.totalElements || 0;
          }
          
          console.log('解析后的产品列表:', this.list);
          console.log('解析后的总数:', this.total);
        }).catch(error => {
          this.listLoading = false;
          console.error('获取产品列表失败:', error);
          console.error('错误详情:', {
            message: error.message,
            response: error.response,
            status: error.response && error.response.status,
            data: error.response && error.response.data

          });
          
          // 显示更详细的错误信息
          let errorMessage = '获取产品列表失败';
          if (error.response) {
            const status = error.response.status;
            const data = error.response.data;
            
            if (status === 500) {
              errorMessage = '服务器内部错误 (500)';
              if (data && data.message) {
                errorMessage += ': ' + data.message;
              } else {
                errorMessage += '，请检查后端日志或 Mapper SQL 语法';
              }
            } else if (data && data.message) {
              errorMessage += ': ' + data.message;
            } else {
              errorMessage += ' (状态码: ' + status + ')';
            }
          } else if (error.message) {
            errorMessage += ': ' + error.message;
          }
          
          this.$message({
            message: errorMessage,
            type: 'error',
            duration: 5000
          });
        });
      },
      getBrandList() {
        fetchBrandList({pageNum: 1, pageSize: 100}).then(response => {
          console.log('品牌列表API响应:', response);
          
          this.brandOptions = [];
          
          // 后端返回格式：{ code: 1, msg: null, data: ["Yonex", "Wilson", ...] }
          // 响应拦截器返回的是 response.data，所以 response = { code: 1, msg: null, data: [...] }
          let brandList = [];
          
          if (response) {
            // 如果 response.data 是数组（字符串数组或对象数组）
            if (response.data && Array.isArray(response.data)) {
              brandList = response.data;
            }
            // 如果 response 本身就是数组（拦截器可能已经处理过）
            else if (Array.isArray(response)) {
              brandList = response;
            }
            // 如果 response.data 是 PageResult 对象
            else if (response.data) {
              brandList = response.data.records || response.data.list || response.data.items || [];
            }
            // 如果 response 本身就是 PageResult 对象
            else if (response.records) {
              brandList = response.records;
            } else if (response.list) {
              brandList = response.list;
            }
          }
          
          console.log('解析后的品牌列表:', brandList);
          console.log('品牌列表长度:', brandList.length);
          
          if (brandList && brandList.length > 0) {
            for (let i = 0; i < brandList.length; i++) {
              const brand = brandList[i];
              
              // 处理字符串数组的情况：["Yonex", "Wilson", ...]
              if (typeof brand === 'string') {
                this.brandOptions.push({
                  label: brand,
                  value: brand  // 使用品牌名称作为 value，或者使用索引 i
                });
              }
              // 处理对象数组的情况：[{ id: 1, name: "Yonex" }, ...]
              else if (typeof brand === 'object' && brand !== null) {
                this.brandOptions.push({
                  label: brand.name || brand.brandName || brand.label || '未知品牌',
                  value: brand.id || brand.value || brand.name || i
                });
              }
            }
          } else {
            console.warn('品牌列表为空或格式不正确');
          }
          
          console.log('最终品牌选项:', this.brandOptions);
          console.log('品牌选项数量:', this.brandOptions.length);
        }).catch(error => {
          console.error('获取品牌列表失败:', error);
          console.error('错误详情:', {
            message: error.message,
            response: error.response,
            status: error.response && error.response.status,
            data: error.response && error.response.data
          });
          this.$message({
            message: '获取品牌列表失败，请检查后端接口 /admin/brand/list',
            type: 'warning',
            duration: 3000
          });
        });
      },
      getProductCateList() {
        // 优先使用新的分类接口 /admin/category/list
        fetchCategoryList().then(response => {
          console.log('分类接口响应:', response);
          this.processCategoryListResponse(response);
        }).catch(error => {
          console.warn('接口 /admin/category/list 失败，尝试备用接口:', error);
          // 备用方案1：尝试 /productCategory/list/withChildren
          return fetchListWithChildren().then(response => {
            this.processProductCateResponse(response);
          }).catch(error2 => {
            console.warn('接口 /productCategory/list/withChildren 失败，尝试备用接口2:', error2);
            // 备用方案2：尝试 /admin/productCategory/list/withChildren
            return fetchListWithChildrenAdmin().then(response => {
              this.processProductCateResponse(response);
            }).catch(error3 => {
              console.warn('接口 /admin/productCategory/list/withChildren 失败，尝试备用接口3:', error3);
              // 备用方案3：尝试 /admin/productCategory/list
              return fetchListAll().then(response => {
                this.processProductCateResponse(response);
              }).catch(error4 => {
                console.error('所有产品分类接口都失败:', error4);
                this.$message({
                  message: '获取产品分类列表失败，请检查后端是否提供了分类接口',
                  type: 'warning',
                  duration: 3000
                });
              });
            });
          });
        });
      },
      processCategoryListResponse(response) {
        let list = [];
        if (response && response.data) {
          if (Array.isArray(response.data)) {
            list = response.data;
          } else if (Array.isArray(response.data.records)) {
            list = response.data.records;
          } else if (Array.isArray(response.data.list)) {
            list = response.data.list;
          }
        } else if (Array.isArray(response)) {
          list = response;
        }
        this.productCateOptions = [];
        if (list && list.length > 0 && typeof list[0] === 'object') {
          for (let i = 0; i < list.length; i++) {
            const item = list[i];
            this.productCateOptions.push({
              label: item.name || item.label,
              value: item.id || item.value
            });
          }
        } else if (list && list.length > 0) {
          for (let i = 0; i < list.length; i++) {
            const id = typeof list[i] === 'number' ? list[i] : (Number(list[i]) || list[i]);
            this.productCateOptions.push({ label: `分类${id}`, value: id });
          }
          fetchListWithChildrenAdmin().then(r => {
            this.processProductCateResponse(r);
          }).catch(() => {
            fetchListWithChildren().then(r2 => {
              this.processProductCateResponse(r2);
            });
          });
        }
      },
      processProductCateResponse(response) {
        console.log('产品分类API响应:', response);
        
        // 后端返回 Result<List>，response.data 就是分类列表
        let list = [];
        if (response && response.data) {
          // 如果 response.data 是数组，直接使用
          if (Array.isArray(response.data)) {
            list = response.data;
          } else if (response.data.list && Array.isArray(response.data.list)) {
            // 如果包装在 list 字段中
            list = response.data.list;
          } else if (response.data.records && Array.isArray(response.data.records)) {
            // 如果包装在 records 字段中（分页数据）
            list = response.data.records;
          }
        } else if (Array.isArray(response)) {
          list = response;
        }
        
        console.log('解析后的分类列表:', list);
        
        this.productCateOptions = [];
        for (let i = 0; i < list.length; i++) {
          let children = [];
          if (list[i].children != null && list[i].children.length > 0) {
            for (let j = 0; j < list[i].children.length; j++) {
              children.push({
                label: list[i].children[j].name,
                value: list[i].children[j].id
              });
            }
          }
          this.productCateOptions.push({
            label: list[i].name,
            value: list[i].id,
            children: children
          });
        }
        
        console.log('分类选项:', this.productCateOptions);
      },
      handleSearchList() {
        this.listQuery.pageNum = 1;
        this.getList();
      },
      handleAddProduct() {
        this.$router.push({ path: '/admin/pms/addProduct' });
      },
      handleBatchOperate() {
        if(this.operateType==null){
          this.$message({
            message: '请选择操作类型',
            type: 'warning',
            duration: 1000
          });
          return;
        }
        if(this.multipleSelection==null||this.multipleSelection.length<1){
          this.$message({
            message: '请选择要操作的球拍',
            type: 'warning',
            duration: 1000
          });
          return;
        }
        this.$confirm('是否要进行该批量操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          let ids=[];
          for(let i=0;i<this.multipleSelection.length;i++){
            ids.push(this.multipleSelection[i].productId || this.multipleSelection[i].id);
          }
          switch (this.operateType) {
            case this.operates[0].value:
              this.updatePublishStatus(1,ids);
              break;
            case this.operates[1].value:
              this.updatePublishStatus(0,ids);
              break;
            case this.operates[2].value:
              this.updateRecommendStatus(1,ids);
              break;
            case this.operates[3].value:
              this.updateRecommendStatus(0,ids);
              break;
            case this.operates[4].value:
              this.updateNewStatus(1,ids);
              break;
            case this.operates[5].value:
              this.updateNewStatus(0,ids);
              break;
            case this.operates[6].value:
              break;
            case this.operates[7].value:
              this.deleteProduct(1,ids);
              break;
            default:
              break;
          }
          this.getList();
        });
      },
      handleSizeChange(val) {
        this.listQuery.pageNum = 1;
        this.listQuery.pageSize = val;
        this.getList();
      },
      handleCurrentChange(val) {
        this.listQuery.pageNum = val;
        this.getList();
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      handlePublishStatusChange(index, row) {
        updateStatus({ publishStatus: row.publishStatus }, row.productId).then(() => {
          this.$message({ message: '修改成功', type: 'success', duration: 1000 });
        });
      },
      handleNewStatusChange(index, row) {
        updateStatus({ newStatus: row.newStatus }, row.productId).then(() => {
          this.$message({ message: '修改成功', type: 'success', duration: 1000 });
        });
      },
      handleRecommendStatusChange(index, row) {
        updateStatus({ recommendStatus: row.recommandStatus }, row.productId).then(() => {
          this.$message({ message: '修改成功', type: 'success', duration: 1000 });
        });
      },
      handleResetSearch() {
        this.listQuery = Object.assign({}, defaultListQuery);
        this.getList();
      },
      handleDelete(index, row) {
  this.$confirm('确认要删除该球拍吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    deleteProduct(row.productId).then(() => {
      this.$message({
        message: '删除成功',
        type: 'success'
      });
      this.getList(); // 重新刷新表格
    }).catch(error => {
      console.error('删除失败:', error);
      this.$message({
        message: '删除失败，请检查后端接口',
        type: 'error'
      });
    });
  });
},
      handleUpdateProduct(index,row){
        // 提供快速审核通过或进入编辑页面两种选项
        this.$confirm('请选择操作：确定 = 直接通过审核；取消 = 进入编辑页', '编辑/审核', {
          confirmButtonText: '直接通过审核',
          cancelButtonText: '进入编辑页',
          type: 'warning'
        }).then(() => {
          // 直接将 auditStatus 设置为 1（通过）并提交更新
          const payload = { auditStatus: 1 };
          updateProduct(row.productId || row.id, payload).then(() => {
            this.$message({ message: '审核已通过', type: 'success', duration: 1000 });
            this.getList();
          }).catch(error => {
            console.error('审核通过失败:', error);
            this.$message({ message: '审核失败，请检查后端接口', type: 'error' });
          });
        }).catch(() => {
          // 取消时进入编辑页
          this.$router.push({ path: '/admin/pms/updateProduct', query: { id: row.productId || row.id } });
        });
      },
      handleShowProduct(index,row){
        console.log("handleShowProduct",row);
      },
      handleShowVerifyDetail(index,row){
        console.log("handleShowVerifyDetail",row);
      },
      handleShowLog(index,row){
        console.log("handleShowLog",row);
      },
      previewImage(row) {
        // 支持 albumPics（逗号分隔）或单一 imageUrl 字段
        const imgs = [];
        if (row.albumPics) {
          if (typeof row.albumPics === 'string') {
            row.albumPics.split(',').forEach(p => { if (p) imgs.push(p); });
          } else if (Array.isArray(row.albumPics)) {
            row.albumPics.forEach(p => { if (p) imgs.push(p); });
          }
        }
        if (imgs.length === 0 && row.imageUrl) imgs.push(row.imageUrl);
        if (imgs.length === 0 && row.pic) imgs.push(row.pic);
        this.previewList = imgs;
        this.previewIndex = 0;
        this.previewVisible = true;
      },
      prevPreview() {
        if (this.previewList.length <= 1) return;
        this.previewIndex = (this.previewIndex - 1 + this.previewList.length) % this.previewList.length;
      },
      nextPreview() {
        if (this.previewList.length <= 1) return;
        this.previewIndex = (this.previewIndex + 1) % this.previewList.length;
      },
      updatePublishStatus(publishStatus, ids) {
        const tasks = ids.map(id => updateStatus({ publishStatus }, id));
        Promise.all(tasks).then(() => {
          this.$message({ message: '修改成功', type: 'success', duration: 1000 });
        });
      },
      updateNewStatus(newStatus, ids) {
        const tasks = ids.map(id => updateStatus({ newStatus }, id));
        Promise.all(tasks).then(() => {
          this.$message({ message: '修改成功', type: 'success', duration: 1000 });
        });
      },
      updateRecommendStatus(recommendStatus, ids) {
        const tasks = ids.map(id => updateStatus({ recommendStatus }, id));
        Promise.all(tasks).then(() => {
          this.$message({ message: '修改成功', type: 'success', duration: 1000 });
        });
      },
      deleteProduct(deleteStatus, ids) {
        let params = new URLSearchParams();
        params.append('ids', ids);
        params.append('deleteStatus', deleteStatus);
        deleteProduct(params).then(response => {
          this.$message({
            message: '删除成功',
            type: 'success',
            duration: 1000
          });
        });
        this.getList();
      }
    }
  }
</script>
<style></style>
