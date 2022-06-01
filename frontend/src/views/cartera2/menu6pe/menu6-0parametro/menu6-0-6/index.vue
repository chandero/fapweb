<template>
  <el-container>
    <el-header>
      <h1>VARIABLE INDEPENDIENTE</h1>
    </el-header>
    <el-main>
      <el-row>
        <el-col :span="24">
          <el-table :data="tableData" stripe max-height="600">
            <el-table-column label="ID" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.VAR_INDEPENDIENTE_ID }}</span>
              </template>
            </el-table-column>
            <el-table-column label="CODIGO" width="150">
              <template slot-scope="scope">
                <span>{{ scope.row.VAR_INDEPENDIENTE_CODIGO }}</span>
              </template>
            </el-table-column>
            <el-table-column label="COEFICIENTE" width="150" align="left">
              <template slot-scope="scope">
                <span>{{ scope.row.VAR_INDEPENDIENTE_COEFICIENTE }}</span>
              </template>
            </el-table-column>
            <el-table-column
              label="TIPO CARTERA"
              width="150"
              header-align="center"
              align="left"
            >
              <template slot-scope="scope">
                <span>{{ TipoCartera(scope.row.TIPO_CARTERA_ID) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="ES LIBRANZA" width="150" align="center">
              <template slot-scope="scope">
                <el-checkbox
                  disabled
                  v-model="scope.row.VAR_INDEPENDIENTE_LIBRANZA"
                ></el-checkbox>
              </template>
            </el-table-column>
            <el-table-column label="ORDEN" width="80">
              <template slot-scope="scope">
                <span>{{ scope.row.VAR_INDEPENDIENTE_ORDEN }}</span>
              </template>
            </el-table-column>
            <el-table-column label="ES ACTIVA" width="150" align="center">
              <template slot-scope="scope">
                <el-checkbox
                  disabled
                  v-model="scope.row.VAR_INDEPENDIENTE_ACTIVA"
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
import { getVarIndependiente } from "@/api/pe";
import { gTipoCartera } from "@/utils/global_functions";

export default {
  data() {
    return {
      tableData: [],
    };
  },
  mounted() {
    getVarIndependiente().then((response) => {
      this.tableData = response.data;
    });
  },
  methods: {
    TipoCartera(id) {
      return gTipoCartera(id);
    },
  },
};
</script>