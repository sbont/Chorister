/* eslint-disable @typescript-eslint/no-explicit-any */
declare module 'pagedjs' {
  interface FlowResult {
    total: number;
    performance: number;
    size: { width: { value: number; unit: string }; height: { value: number; unit: string } };
    pages: unknown[];
  }

  export class Previewer {
    constructor(options?: Record<string, unknown>);

    preview(
      content?: DocumentFragment | Node,
      stylesheets?: Array<string | Record<string, string>>,
      renderTo?: HTMLElement,
    ): Promise<FlowResult>;
  }

  export class Chunker {
    constructor(content?: Node, renderTo?: HTMLElement, options?: Record<string, unknown>);

    flow(content: Node, renderTo: HTMLElement): Promise<FlowResult>;
    pages: unknown[];
    total: number;
  }

  export class Polisher {
    constructor(setup?: boolean);
    setup(): void;
    add(...stylesheets: Array<string | Record<string, string>>): Promise<void>;
  }

  export class Previewer {
    constructor(options?: any);

    settings: any;
    polisher: Polisher;
    chunker: Chunker;
    hooks: Handler[];
    size: {
      width: {
        value: number;
        unit: string;
      };
      height: {
        value: number;
        unit: string;
      };
      format: any;
      orientation: any;
    };
    initializeHandlers(): any;
    atpages: any;
    registerHandlers(...handles: Handler[]): any;
    getParams(name: string): any;
    wrapContent(): any;
    removeStyles(doc?: Document): any[];
    preview(content?: DocumentFragment, stylesheets?: any, renderTo?: Element): Promise<any>;
    handlers: any;
  }


  // beforePreview(content, renderTo);
  // afterPreview(pages);

  // // Chunker
  // beforeParsed(content);
  // afterParsed(parsed);
  // beforePageLayout(page);
  // afterPageLayout(pageElement, page, breakToken);
  // afterRendered(pages);

  // // Polisher
  // beforeTreeParse(text, sheet);
  // beforeTreeWalk(ast);
  // afterTreeWalk(ast, sheet);
  // onUrl(urlNode);
  // onAtPage(atPageNode);
  // onRule(ruleNode);
  // onDeclaration(declarationNode, ruleNode);
  // onContent(contentNode, declarationNode, ruleNode);

  // // Layout
  // layoutNode(node);
  // renderNode(node, sourceNode);

  type Overflow = {
    collapsed: true,
    commonAncestorContainer: Element
    endContainer: Element
    endOffset: number
    startContainer: Element
    startOffset: number
  }

  export type Chunker = {
    settings: any;
    hooks: object;
    pages: any[];
    total: number;
    q: any;
    stopped: boolean;
    rendered: boolean;
    content: any;
    charsPerBreak: any[];
    setup(renderTo: any): void;
    pagesArea: HTMLDivElement;
    pageTemplate: HTMLTemplateElement;
    flow(content: any, renderTo: any): Promise<Chunker>;
    source: any;
    breakToken: any;
    render(parsed: any, startAt: any): Promise<any>;
    start(): void;
    stop(): void;
    renderOnIdle(renderer: any): any;
    renderAsync(renderer: any): Promise<any>;
    handleBreaks(node: any, force: any): Promise<void>;
    layout(content: any, startAt: any): object;
    recoredCharLength(length: any): void;
    maxChars: number;
    removePages(fromIndex?: number): void;
    addPage(blank: any): any;
    clonePage(originalPage: any): Promise<void>;
    loadFonts(): any;
    destroy(): void;
  }

  export type Page = {
    pagesArea: any;
    pageTemplate: any;
    blank: any;
    width: number;
    height: number;
    hooks: any;
    settings: any;
    create(template: any, after: any): any;
    element: Element;
    pagebox: any;
    area: any;
    footnotesArea: any;
    createWrapper(): HTMLDivElement;
    wrapper: HTMLDivElement;
    index(pgnum: any): void;
    position: any;
    id: string;
    layout(contents: any, breakToken: any, maxChars: any): Promise<any>;
    startToken: any;
    layoutMethod: Layout;
    endToken: any;
    append(contents: any, breakToken: any): Promise<any>;
    getByParent(ref: any, entries: any): any;
    onOverflow(func: any): void;
    _onOverflow: any;
    onUnderflow(func: any): void;
    _onUnderflow: any;
    clear(): void;
    addListeners(contents: any): boolean;
    _checkOverflowAfterResize: any;
    _onScroll: any;
    listening: boolean;
    removeListeners(): void;
    addResizeObserver(contents: any): void;
    ro: ResizeObserver;
    checkOverflowAfterResize(contents: any): void;
    checkUnderflowAfterResize(contents: any): void;
    destroy(): void;
  }

  export type Layout = {
    element: any;
    bounds: DOMRect;
    parentBounds: any;
    gap: number;
    hooks: any;
    settings: any;
    maxChars: any;
    forceRenderBreak: boolean;
    renderTo(wrapper: any, source: any, breakToken: any, bounds?: any): Promise<any>;
    breakAt(node: any, offset?: number): any;
    shouldBreak(node: any, limiter: any): any;
    forceBreak(): void;
    getStart(source: any, breakToken: any): any;
    append(node: any, dest: any, breakToken: any, shallow?: boolean, rebuild?: boolean): any;
    rebuildTableFromBreakToken(breakToken: any, dest: any): void;
    waitForImages(imgs: any): Promise<void>;
    awaitImageLoaded(image: any): Promise<any>;
    avoidBreakInside(node: any, limiter: any): any;
    createBreakToken(overflow: any, rendered: any, source: any): any;
    findBreakToken(rendered: any, source: any, bounds: any, prevBreakToken: any, extract?: boolean): any;
    hasOverflow(element: any, bounds?: any): boolean;
    findOverflow(rendered: any, bounds?: any, gap?: number): Range;
    findEndToken(rendered: any, source: any): any;
    textBreak(node: any, start: any, end: any, vStart: any, vEnd: any): any;
    removeOverflow(overflow: any, breakLetter: any): any;
    hyphenateAtBreak(startContainer: any, breakLetter: any): void;
    equalTokens(a: any, b: any): boolean;
  }

  type BreakToken = {
    node: Element,
    offset: number
  }

  interface OnOverflow {
    onOverflow(overflow: Overflow | undefined, rendered: Element, bounds: DOMRect);
  }

  // onBreakToken(breakToken, overflow, rendered);

  export class Handler {
    constructor(chunker: Chunker, polisher: Polisher, caller: any);
    afterPageLayout(pageElement: HTMLElement, page: any, breakToken: any): void;
  }

  export const registeredHandlers: Handler[];
  export function registerHandlers(...handles: Handler[])
  export function initializeHandlers(chunker: Previewer, polisher: Polisher, caller: any)
}