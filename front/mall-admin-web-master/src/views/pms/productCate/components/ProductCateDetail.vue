<template>
  <el-card class="form-container" shadow="never">
    <el-form :model="productCate"
             :rules="rules"
             ref="productCateForm"
             label-width="120px">
      <el-form-item label="分类名称" prop="name">
        <el-input v-model="productCate.name"></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="onSubmit('productCateForm')">
          {{ isEdit ? '修改' : '新增' }}
        </el-button>
        <el-button @click="resetForm('productCateForm')">重置</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import { createProductCate, updateProductCate, getProductCateById } from '@/api/productCate';

export default {
  name: "ProductCateDetail",
  props: {
    isEdit: { type: Boolean, default: false }
  },
  data() {
    return {
      productCate: { name: '' },
      rules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    if (this.isEdit) {
      const id = this.$route.query.id;
      if (id) {
        getProductCateById(id).then(res => {
          this.productCate = res.data; // 回显数据
        });
      }
    }
  },
  methods: {
    onSubmit(formName) {
      this.$refs[formName].validate(valid => {
        if (!valid) return;

        if (this.isEdit) {
          // 修改
          updateProductCate(this.$route.query.id, this.productCate).then(() => {
            this.$message.success('修改成功');
            this.$router.back();
          }).catch(err => {
            this.$message.error('修改失败: ' + err);
          });
        } else {
          // 新增
          createProductCate(this.productCate).then(() => {
            this.$message.success('新增成功');
            this.resetForm(formName);
          }).catch(err => {
            this.$message.error('新增失败: ' + err);
          });
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.productCate = { name: '' };
    }
  }
}
</script>
