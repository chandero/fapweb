<template>
  <el-container>
    <el-header>
      <h1>TIPO CARTERA</h1>
    </el-header>
    <el-main>
      <el-row>
        <el-col :span="24">
          <el-table :data="tableData" stripe max-height="600">
            <el-table-column label="ID" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.TIPO_CARTERA_ID }}</span>
              </template>
            </el-table-column>
            <el-table-column label="DESCRIPCION" width="280" align="left">
              <template slot-scope="scope">
                <span>{{ scope.row.TIPO_CARTERA_DESCRIPCION }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>
    </el-main>
  </el-container>
</template>
<script>
import { getTipoCartera } from "@/api/pe";

export default {
  data() {
    return {
      tableData: [],
    };
  },
  mounted() {
    getTipoCartera().then((response) => {
      this.tableData = response.data;
    });
  },
  methods: {},
};
</script>