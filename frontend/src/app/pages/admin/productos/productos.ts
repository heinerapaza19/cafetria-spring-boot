import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AdminSidebar } from '../../../shared/admin-sidebar/admin-sidebar';
import { ProductoService } from '../../../core/services/producto';
import { CategoriaService } from '../../../core/services/categoria';
import { Producto, Categoria } from '../../../core/models';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [CommonModule, FormsModule, AdminSidebar],
  templateUrl: './productos.html',
  styleUrl: './productos.scss',
})
export class Productos implements OnInit {
  private productoSvc = inject(ProductoService);
  private categoriaSvc = inject(CategoriaService);

  productos = signal<Producto[]>([]);
  categorias = signal<Categoria[]>([]);
  loading = signal(true);
  showModal = signal(false);
  saving = signal(false);

  mensaje = signal('');
  mensajeTipo = signal<'success' | 'error'>('success');

  form = {
    id: 0,
    nombre: '',
    descripcion: '',
    precio: 0,
    stock: 0,
    idCategoria: 0,
  };

  imagenFile: File | null = null;
  editMode = false;

  private readonly URL_BACKEND = 'http://localhost:7091';

  ngOnInit() {
    this.cargarProductos();
    this.cargarCategorias();
  }

  cargarProductos() {
    this.loading.set(true);

    this.productoSvc.getAll().subscribe({
      next: (data) => {
        console.log('Productos recibidos:', data);
        this.productos.set(data);
        this.loading.set(false);
      },
      error: (error) => {
        console.error('Error cargando productos:', error);
        this.loading.set(false);
        this.mostrarMensaje('Error al cargar productos', 'error');
      },
    });
  }

  cargarCategorias() {
    this.categoriaSvc.getAll().subscribe({
      next: (data) => {
        this.categorias.set(data);
      },
      error: (error) => {
        console.error('Error cargando categorías:', error);
      },
    });
  }

  abrirModal(p?: Producto) {
    if (p) {
      this.editMode = true;

      this.form = {
        id: p.id,
        nombre: p.nombre,
        descripcion: p.descripcion || '',
        precio: p.precio,
        stock: p.stock,
        idCategoria: p.idCategoria,
      };
    } else {
      this.editMode = false;

      this.form = {
        id: 0,
        nombre: '',
        descripcion: '',
        precio: 0,
        stock: 0,
        idCategoria: 0,
      };
    }

    this.imagenFile = null;
    this.showModal.set(true);
  }

  onFileChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.imagenFile = input.files?.[0] || null;
  }

  guardar() {
    if (!this.form.nombre.trim()) {
      this.mostrarMensaje('El nombre es obligatorio', 'error');
      return;
    }

    if (this.form.precio <= 0) {
      this.mostrarMensaje('El precio debe ser mayor a 0', 'error');
      return;
    }

    this.saving.set(true);

    const fd = new FormData();
    fd.append('nombre', this.form.nombre);
    fd.append('descripcion', this.form.descripcion);
    fd.append('precio', String(this.form.precio));
    fd.append('stock', String(this.form.stock));
    fd.append('idCategoria', String(this.form.idCategoria));

    if (this.imagenFile) {
      fd.append('imagen', this.imagenFile);
    }

    const op = this.editMode
      ? this.productoSvc.update(this.form.id, fd)
      : this.productoSvc.create(fd);

    op.subscribe({
      next: () => {
        this.saving.set(false);
        this.showModal.set(false);

        this.mostrarMensaje(this.editMode ? 'Producto actualizado' : 'Producto creado', 'success');

        this.cargarProductos();
      },
      error: (error) => {
        console.error('Error guardando producto:', error);
        this.saving.set(false);
        this.mostrarMensaje('Error al guardar producto', 'error');
      },
    });
  }

  eliminar(id: number) {
    const confirmar = confirm('¿Eliminar este producto?');

    if (!confirmar) return;

    this.productoSvc.delete(id).subscribe({
      next: () => {
        this.mostrarMensaje('Producto eliminado', 'success');
        this.cargarProductos();
      },
      error: (error) => {
        console.error('Error eliminando producto:', error);
        this.mostrarMensaje('Error al eliminar producto', 'error');
      },
    });
  }

  mostrarMensaje(msg: string, tipo: 'success' | 'error') {
    this.mensaje.set(msg);
    this.mensajeTipo.set(tipo);

    setTimeout(() => {
      this.mensaje.set('');
    }, 3000);
  }

  getImgUrl(img?: string | null): string {
    if (!img) return '';

    if (img.startsWith('http')) {
      return img;
    }

    if (img.startsWith('/uploads/productos/')) {
      return `${this.URL_BACKEND}${img}`;
    }

    if (img.startsWith('assets/')) {
      return img;
    }

    return `${this.URL_BACKEND}/uploads/productos/${img}`;
  }

  onImgError(event: Event, img?: string | null) {
    const image = event.target as HTMLImageElement;

    if (!img) {
      this.mostrarPlaceholder(image);
      return;
    }

    const yaIntentoAssets = image.dataset['fallback'] === 'assets';

    if (!yaIntentoAssets) {
      image.dataset['fallback'] = 'assets';
      image.src = `assets/img/productos/${img}`;
      return;
    }

    this.mostrarPlaceholder(image);
  }

  private mostrarPlaceholder(image: HTMLImageElement) {
    image.style.display = 'none';

    const parent = image.parentElement;

    if (parent) {
      parent.innerHTML = `
        <div class="table-img-placeholder">
          <i class="fas fa-coffee"></i>
        </div>
      `;
    }
  }
}
