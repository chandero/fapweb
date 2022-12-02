<template>
  <el-container>
    <el-main>
      <el-card style="border: 1px solid #b3b0b0;">
      <el-row :gutter="4">
        <el-col :span="24">
          <el-input placeholder="Filtrar por" prefix-icon="el-icon-search" v-model="filterText"></el-input>
          <el-tree
            class="filter-tree"
            :data="pucdata"
            :props="defaultProps"
            :filter-node-method="filterNode"
            :default-expanded-keys="[0]"
            @node-click="handleNodeClick"
            expand-on-click-node
            check-on-click-node
            check-strictly
            highlight-current
            node-key="id"
            ref="tree"
          ></el-tree>
        </el-col>
      </el-row>
      <el-row>
          <el-col>
              <el-button :disabled="!selectable" type="primary" icon="el-icon-circle-check" @click="seleccionar()">Seleccionar</el-button>
          </el-col>
      </el-row>
      </el-card>
    </el-main>
  </el-container>
</template>
<script>
import { obtenerPuc } from "@/api/puc";

export default {
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val);
    },
  },
  data() {
    return {
      filterText: "",
      pucdata: [
        { id: 0, label: "Plan Único de Cuentas", disabled: true, children: [] },
      ],
      puc: null,
      defaultProps: {
        children: "children",
        label: "label",
        disabled: "disabled",
      },
      codigo: null,
      selectable: false
    };
  },
  beforeMount() {
    this.buscarPuc();
  },
  methods: {
    seleccionar() {
        this.$emit('selected', this.codigo);
    },
    handleNodeClick(data) {
      if (!data.disabled) {
          this.codigo = data.id;
          this.selectable = true;
      } else {
          this.codigo = null;
          this.selectable = false;
      }
    },
    filterNode(value, data) {
      if (!value) return true;
      return data.label.indexOf(value) !== -1;
    },
    buscarPuc() {
      obtenerPuc().then((response) => {
        this.puc = response.data;
        this.puc.forEach((p) => {
          if (p.codigo === p.codigomayor) {
            this.pucdata[0].children.push({
              id: p.codigo,
              label: p.codigo + " -> " + p.nombre,
              mayor: null,
              disabled: p.movimiento ? false : true,
              children: [],
            });
          } else {
            var n = this.buscarNodo(this.pucdata[0], p.codigomayor);
            if (n) {
              n.children.push({
                id: p.codigo,
                label: p.codigo + " -> " + p.nombre,
                mayor: p.codigomayor,
                disabled: p.movimiento ? false : true,
                children: [],
              });
            }
          }
        });
      });
    },
    buscarNodo(currChild, searchString) {
      if (currChild.id == searchString) {
        return currChild;
      } else if (currChild.children != null) {
        var i;
        var result = null;
        for (i = 0; result == null && i < currChild.children.length; i++) {
          result = this.buscarNodo(currChild.children[i], searchString);
        }
        return result;
      }
      return null;
    },
  },
};
</script>
<style lang="scss" scoped>
.filter-tree {
    max-height: 400px; 
    max-width: 600px; 
    overflow: auto;
}
span {
    .el-tree-node__label {
       font-size: 12px !important;
    }
}
</style>