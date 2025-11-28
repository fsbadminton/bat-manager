<template>
  <div style="margin-top: 50px">
    <el-form :model="value" :rules="rules" ref="productInfoForm" label-width="120px" class="form-inner-container" size="small">
      <el-form-item label="球拍分类：" prop="productCategoryId">
        <el-select
          v-model="value.productCategoryId"
          @change="handleCategoryChange"
          placeholder="请选择分类">
          <el-option
            v-for="item in productCateOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="球拍名称：" prop="name">
        <el-input v-model="value.name"></el-input>
      </el-form-item>
      <el-form-item label="球拍品牌：" prop="brandId">
        <el-select
          v-model="value.brandId"
          @change="handleBrandChange"
          placeholder="请选择品牌">
          <el-option
            v-for="item in brandOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="球拍介绍：">
        <el-input
          :autoSize="true"
          v-model="value.description"
          type="textarea"
          placeholder="请输入内容"></el-input>
      </el-form-item>
      <el-form-item label="球拍货号：">
        <el-input v-model="value.productSn"></el-input>
      </el-form-item>
      <el-form-item label="球拍售价：">
        <el-input v-model="value.price"></el-input>
      </el-form-item>
      <el-form-item label="球拍库存：">
        <el-input v-model="value.stock"></el-input>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button type="primary" size="medium" @click="handleNext('productInfoForm')">下一步，填写球拍属性</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
  import {fetchListAll, fetchListWithChildren, fetchListWithChildrenAdmin} from '@/api/productCate'
  import {fetchBrandList } from '@/api/brand'
  import {getProduct} from '@/api/product';

  export default {
    name: "ProductInfoDetail",
    props: {
      value: Object,
      isEdit: {
        type: Boolean,
        default: false
      }
    },
    data() {
      return {
        hasEditCreated:false,
        //选中商品分类的值
        selectProductCateValue: [],
        productCateOptions: [],
        brandOptions: [],
        rules: {
          name: [
            {required: true, message: '请输入商品名称', trigger: 'blur'},
            {min: 2, max: 140, message: '长度在 2 到 140 个字符', trigger: 'blur'}
          ],
          subTitle: [{required: true, message: '请输入商品副标题', trigger: 'blur'}],
          productCategoryId: [{required: true, message: '请选择商品分类', trigger: 'blur'}],
          brandId: [{required: true, message: '请选择商品品牌', trigger: 'blur'}],
          description: [{required: true, message: '请输入商品介绍', trigger: 'blur'}],
          requiredProp: [{required: true, message: '该项为必填项', trigger: 'blur'}]
        }
      };
    },
    created() {
      console.log("🔥 created 执行了 ProductInfoDetail");
      this.getProductCateList();
      this.getBrandList();
    },
    computed:{
      //商品的编号
      productId(){
        return this.value.id;
      }
    },
    watch: {
      productId:function(newValue){
        if(!this.isEdit)return;
        if(this.hasEditCreated)return;
        if(newValue===undefined||newValue==null||newValue===0)return;
        this.handleEditCreated();
      },
      'value.category': function(newVal) {
        if (newVal == null) return;
        // 如果分类选项已加载，则尝试映射
        if (this.productCateOptions && this.productCateOptions.length > 0) {
          const categoryId = newVal;
          let matched = false;
          for (let p = 0; p < this.productCateOptions.length; p++) {
            const parent = this.productCateOptions[p];
            if (parent.value === categoryId) {
              this.value.productCategoryId = categoryId;
              matched = true;
              break;
            }
            if (parent.children && Array.isArray(parent.children)) {
              const child = parent.children.find(c => c.value === categoryId);
              if (child) {
                this.value.productCategoryId = child.value;
                this.selectProductCateValue = [parent.value, child.value];
                matched = true;
                break;
              }
            }
          }
          if (!matched) {
            this.value.productCategoryId = categoryId;
          }
          this.value.productCategoryName = this.getCateNameById(this.value.productCategoryId);
        }
      },
      selectProductCateValue: function (newValue) {
        if (newValue != null && newValue.length === 2) {
          this.value.productCategoryId = newValue[1];
          this.value.productCategoryName= this.getCateNameById(this.value.productCategoryId);
        } else {
          this.value.productCategoryId = null;
          this.value.productCategoryName=null;
        }
      }
    },
    methods: {
      //处理编辑逻辑
      handleEditCreated(){
        if(this.value.productCategoryId!=null){
          this.selectProductCateValue.push(this.value.cateParentId);
          this.selectProductCateValue.push(this.value.productCategoryId);
        }
        this.hasEditCreated=true;
      },
      getProductCateList() {
        fetchListAll().then(response => {
          let list = [];
          if (response && response.data) {
            if (Array.isArray(response.data)) {
              list = response.data;
            } else {
              list = response.data.records || response.data.list || [];
            }
          } else if (Array.isArray(response)) {
            list = response;
          }
          this.productCateOptions = [];
          if (list.length > 0 && typeof list[0] === 'object') {
            for (let i = 0; i < list.length; i++) {
              const item = list[i];
              this.productCateOptions.push({ label: item.name || item.label, value: item.id || item.value });
            }
            // 如果后端返回的是 category 字段（例如："category":9），映射到前端使用的 productCategoryId
            if (this.value && (this.value.productCategoryId == null || this.value.productCategoryId === '') && this.value.category != null) {
              // 优先尝试直接匹配子分类或顶级
              const categoryId = this.value.category;
              // 直接匹配顶级或子项
              let matched = false;
              for (let p = 0; p < this.productCateOptions.length; p++) {
                const parent = this.productCateOptions[p];
                if (parent.value === categoryId) {
                  this.value.productCategoryId = categoryId;
                  matched = true;
                  break;
                }
                if (parent.children && Array.isArray(parent.children)) {
                  const child = parent.children.find(c => c.value === categoryId);
                  if (child) {
                    this.value.productCategoryId = child.value;
                    // 如果需要在 UI 上显示父子选择值（某些地方使用 selectProductCateValue），写入它
                    this.selectProductCateValue = [parent.value, child.value];
                    matched = true;
                    break;
                  }
                }
              }
              // 如果仍未匹配，尝试把 category 当作 productCategoryId 使用（容错）
              if (!matched) {
                this.value.productCategoryId = categoryId;
              }
              this.value.productCategoryName = this.getCateNameById(this.value.productCategoryId);
            }
          } else {
            fetchListWithChildrenAdmin().then(r => {
              const data = r && r.data ? r.data : r;
              this.productCateOptions = [];
              for (let i = 0; i < data.length; i++) {
                const children = [];
                if (data[i].children && data[i].children.length > 0) {
                  for (let j = 0; j < data[i].children.length; j++) {
                    children.push({ label: data[i].children[j].name, value: data[i].children[j].id });
                  }
                }
                this.productCateOptions.push({ label: data[i].name, value: data[i].id, children });
              }
                // 映射后端 category 字段到 productCategoryId（支持父/子结构）
                if (this.value && (this.value.productCategoryId == null || this.value.productCategoryId === '') && this.value.category != null) {
                  const categoryId = this.value.category;
                  let matched = false;
                  for (let p = 0; p < this.productCateOptions.length; p++) {
                    const parent = this.productCateOptions[p];
                    if (parent.value === categoryId) {
                      this.value.productCategoryId = categoryId;
                      matched = true;
                      break;
                    }
                    if (parent.children && Array.isArray(parent.children)) {
                      const child = parent.children.find(c => c.value === categoryId);
                      if (child) {
                        this.value.productCategoryId = child.value;
                        this.selectProductCateValue = [parent.value, child.value];
                        matched = true;
                        break;
                      }
                    }
                  }
                  if (!matched) {
                    this.value.productCategoryId = categoryId;
                  }
                  this.value.productCategoryName = this.getCateNameById(this.value.productCategoryId);
                }
            }).catch(() => {
              fetchListWithChildren().then(r2 => {
                const data2 = r2 && r2.data ? r2.data : r2;
                this.productCateOptions = [];
                for (let i = 0; i < data2.length; i++) {
                  const children = [];
                  if (data2[i].children && data2[i].children.length > 0) {
                    for (let j = 0; j < data2[i].children.length; j++) {
                      children.push({ label: data2[i].children[j].name, value: data2[i].children[j].id });
                    }
                  }
                  this.productCateOptions.push({ label: data2[i].name, value: data2[i].id, children });
                }
                // 映射后端 category 字段到 productCategoryId（支持父/子结构）
                if (this.value && (this.value.productCategoryId == null || this.value.productCategoryId === '') && this.value.category != null) {
                  const categoryId = this.value.category;
                  let matched = false;
                  for (let p = 0; p < this.productCateOptions.length; p++) {
                    const parent = this.productCateOptions[p];
                    if (parent.value === categoryId) {
                      this.value.productCategoryId = categoryId;
                      matched = true;
                      break;
                    }
                    if (parent.children && Array.isArray(parent.children)) {
                      const child = parent.children.find(c => c.value === categoryId);
                      if (child) {
                        this.value.productCategoryId = child.value;
                        this.selectProductCateValue = [parent.value, child.value];
                        matched = true;
                        break;
                      }
                    }
                  }
                  if (!matched) {
                    this.value.productCategoryId = categoryId;
                  }
                  this.value.productCategoryName = this.getCateNameById(this.value.productCategoryId);
                }
              });
            });
          }
        });
      },
      getBrandList() {
        fetchBrandList({pageNum: 1, pageSize: 100}).then(response => {
          this.brandOptions = [];
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
            }else if(response.data&&Array.isArray(response.data.records)){
              brandList = response.data.records;
            }
          }
          this.brandOptions = brandList.map(item => ({
          value: item.id,
          label: item.name
          }))
          // 如果当前 value 中包含 brandName（后端可能直接返回 brandName），尝试根据名称回写 brandId
          if (this.value && this.value.brandName && (this.value.brandId == null || this.value.brandId === '')) {
            const match = this.brandOptions.find(b => (b.label || '').toString().trim() === (this.value.brandName || '').toString().trim());
            if (match) {
              this.value.brandId = match.value;
            }
          }
          
        }); 
      },
      getCateNameById(id){
        let name = null;
        for (let i = 0; i < this.productCateOptions.length; i++) {
          const opt = this.productCateOptions[i];
          if (opt.value === id) {
            name = opt.label;
            break;
          }
          if (opt.children && Array.isArray(opt.children)) {
            for (let j = 0; j < opt.children.length; j++) {
              if (opt.children[j].value === id) {
                name = opt.children[j].label;
                break;
              }
            }
            if (name) break;
          }
        }
        return name;
      },
      handleNext(formName){
        this.$refs[formName].validate((valid) => {
          if (valid) {
            this.$emit('nextStep');
          } else {
            this.$message({
              message: '验证失败',
              type: 'error',
              duration:1000
            });
            return false;
          }
        });
      },
      handleCategoryChange(val) {
        let categoryName = '';
        for (let i = 0; i < this.productCateOptions.length; i++) {
          if (this.productCateOptions[i].value === val) {
            categoryName = this.productCateOptions[i].label;
            break;
          }
        }
        this.value.productCategoryName = categoryName;
      },
      handleBrandChange(val) {
        let brandName = '';
        for (let i = 0; i < this.brandOptions.length; i++) {
          if (this.brandOptions[i].value === val) {
            brandName = this.brandOptions[i].label;
            break;
          }
        }
        this.value.brandName = brandName;
      }
    }
  }
</script>

<style scoped>
</style>
