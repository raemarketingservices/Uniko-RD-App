import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useState } from "react";
import { useAuth } from "@/lib/auth";
import { fetchTiendas } from "@/lib/queries";

type Message = { id: string; store: string; buyer: string; sender: string; name: string; text: string; date: string };

export const Route = createFileRoute("/mensajes")({
  validateSearch: (s): { tienda?: string | undefined } => ({ tienda: typeof s['tienda'] === 'string' ? s['tienda'] : undefined }),
  loader: async () => ({ tiendas: await fetchTiendas() }),
  head: () => ({
    meta: [{ title: "Mensajes · UNIKO-RD" }, { name: "robots", content: "noindex" }],
  }),
  component: Mensajes,
});

async function request(init: RequestInit = {}) {
  const response = await fetch('/local-api/messages', { ...init, headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${sessionStorage.getItem('unikord.cloudflare.session') ?? ''}` } });
  const data = await response.json();
  if (!response.ok) throw new Error(data.error ?? 'No se pudieron cargar los mensajes.');
  return data;
}

function Mensajes() {
  const { tiendas } = Route.useLoaderData();
  const { tienda } = Route.useSearch();
  const { profile, loading } = useAuth();
  const [store, setStore] = useState(tienda ?? '');
  const [buyer, setBuyer] = useState('');
  const [messages, setMessages] = useState<Message[]>([]);
  const [text, setText] = useState('');
  const [error, setError] = useState('');
  const [sending, setSending] = useState(false);
  useEffect(() => {
    if (!profile) return;
    let active = true;
    const load = () => request().then(data => { if (active) { setMessages(data); setError(''); } }).catch(e => { if (active) setError(e.message); });
    void load();
    const timer = setInterval(load, 10000);
    return () => { active = false; clearInterval(timer); };
  }, [profile?.id]);
  const conversations = messages.filter((m, i, all) => all.findIndex(x => x.store === m.store && x.buyer === m.buyer) === i);
  const visible = messages.filter(m => m.store === store && m.buyer === (buyer || profile?.id));
  if (loading) return <p className="p-8">Cargando…</p>;
  if (!profile) return <div className="mx-auto max-w-xl p-8"><h1 className="text-2xl">Contacta con las tiendas</h1><p className="my-4">Inicia sesión para enviar mensajes y recibir respuestas.</p><Link className="btn-base btn-primary" to="/auth" search={{ redirect: `/mensajes${tienda ? `?tienda=${encodeURIComponent(tienda)}` : ''}` }}>Iniciar sesión o crear cuenta</Link></div>;
  return <div className="mx-auto max-w-7xl px-4 py-8"><h1 className="text-3xl">Mensajes</h1><p className="mt-2 text-muted-foreground">Conversaciones guardadas en este servidor local.</p>
    <div className="mt-6 grid gap-6 md:grid-cols-[280px_1fr]"><aside className="card-uniko space-y-3 p-4"><label className="grid gap-2">Contactar una tienda<select className="select-uniko w-full" value={store} onChange={e => { setStore(e.target.value); setBuyer(''); }}><option value="">Selecciona una tienda</option>{tiendas.map(s => <option key={s.id} value={s.id}>{s.nombre}</option>)}</select></label><h2 className="font-bold">Conversaciones</h2>{conversations.map(m => <button className="block w-full rounded-lg border p-3 text-left hover:bg-muted" key={`${m.store}:${m.buyer}`} onClick={() => { setStore(m.store); setBuyer(m.buyer); }}>{tiendas.find(s => s.id === m.store)?.nombre}<span className="block text-xs text-muted-foreground">{m.buyer === profile.id ? 'Tu consulta' : m.name}</span></button>)}{!conversations.length && <p className="text-sm text-muted-foreground">Todavía no tienes conversaciones.</p>}</aside>
    <section className="card-uniko p-5"><h2 className="text-xl">{tiendas.find(s => s.id === store)?.nombre ?? 'Selecciona una tienda'}</h2><div aria-live="polite" className="my-5 max-h-[450px] min-h-48 space-y-3 overflow-y-auto">{visible.map(m => <div key={m.id} className={`max-w-[85%] rounded-xl p-3 ${m.sender === profile.id ? 'ml-auto bg-primary text-primary-foreground' : 'bg-muted'}`}><p className="text-xs font-bold">{m.name}</p><p className="whitespace-pre-wrap break-words">{m.text}</p><time className="text-xs opacity-70">{new Date(m.date).toLocaleString('es-DO')}</time></div>)}{!visible.length && <p className="text-sm text-muted-foreground">Escribe tu consulta sobre productos, disponibilidad o entrega.</p>}</div>{error && <p role="alert" className="mb-3 text-red-600">{error}</p>}
    <form className="flex gap-3" onSubmit={async e => { e.preventDefault(); if(sending) return; setSending(true); try { const message = await request({ method: 'POST', body: JSON.stringify({ store, buyer, text }) }); setMessages(old => [...old, message]); setText(''); setError(''); } catch (e) { setError(e instanceof Error ? e.message : 'No se pudo enviar.'); } finally { setSending(false); } }}><textarea aria-label="Mensaje" required maxLength={2000} className="input-uniko flex-1" value={text} onChange={e => setText(e.target.value)} placeholder="Escribe tu mensaje…" /><button className="btn-base btn-primary" disabled={!store || sending || !text.trim()}>{sending ? 'Enviando…' : 'Enviar'}</button></form></section></div></div>;
}
