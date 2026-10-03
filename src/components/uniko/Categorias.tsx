import { Link } from "@tanstack/react-router";
import { icons } from "lucide-react";
import { categorias, type Categoria } from "@/data/marketplace";

export function IconoCategoria({ nombre, className }: { nombre: string; className?: string }) {
  const Icono = icons[nombre as keyof typeof icons] ?? icons.Package;
  return <Icono className={className} />;
}

export function GrillaCategorias({ lista = categorias }: { lista?: Categoria[] }) {
  return (
    <div className="grid grid-cols-3 gap-3 sm:grid-cols-4 lg:grid-cols-6">
      {lista.map((cat) => (
        <Link
          key={cat.slug}
          to="/categorias/$categoriaSlug"
          params={{ categoriaSlug: cat.slug }}
          className="card-uniko flex flex-col items-center gap-2 p-4 text-center"
        >
          <span className="grid h-14 w-14 shrink-0 place-items-center rounded-full bg-accent text-primary">
            <IconoCategoria nombre={cat.icono} className="h-6 w-6" />
          </span>
          <span className="text-xs font-semibold text-foreground">{cat.nombre}</span>
        </Link>
      ))}
    </div>
  );
}
