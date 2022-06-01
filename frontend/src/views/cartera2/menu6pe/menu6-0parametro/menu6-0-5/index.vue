<template>
  <el-container>
    <el-header>
      <h1>TIPO GARANTIA</h1>
    </el-header>
    <el-main>
      <el-row>
        <el-col :span="24">
          <el-table :data="tableData" stripe max-height="600">
            <el-table-column label="ID" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.TIPO_GARANTIA_ID }}</span>
              </template>
            </el-table-column>
            <el-table-column label="DESCRIPCION" width="280" align="left">
              <template slot-scope="scope">
                <span>{{ scope.row.TIPO_GARANTIA_DESCRIPCION }}</span>
              </template>
            </el-table-column>
            <el-table-column label="ES IDONEA" width="150">
              <template slot-scope="scope">
                <el-checkbox
                  disabled
                  v-model="scope.row.TIPO_GARANTIA_ES_IDONEA"
                ></el-checkbox>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { getTipoGarantia } from "@/api/pe";

export default {
  data() {
    return {
      tableData: [],
    };
  },
  mounted() {
    getTipoGarantia().then((response) => {
      this.tableData = response.data;
    });
  },
  methods: {},
};
</script>