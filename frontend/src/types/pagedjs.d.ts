/* eslint-disable @typescript-eslint/no-explicit-any */
declare module 'pagedjs' {
  interface FlowResult {
    total: number;
    performance: number;
    size: { width: { value: number; unit: string }; height: { value: number; unit: string } };
    pages: Page[];
  }

  export class Chunker {
    constructor(content?: Node, renderTo?: HTMLElement, options?: Record<string, unknown>);
    flow(content: Node, renderTo: HTMLElement): Promise<FlowResult>;
    pages: Page[];
    total: number;
    destroy(): void;
  }

  export class Polisher {
    constructor(setup?: boolean);
    setup(): void;
    add(...stylesheets: Array<string | Record<string, string>>): Promise<void>;
    destroy(): void;
  }

  export class Previewer {
    constructor(options?: Record<string, unknown>);
    settings: Record<string, unknown>;
    polisher: Polisher;
    chunker: Chunker;
    hooks: Handler[];
    handlers: Handler[];
    size: {
      width: {
        value: number;
        unit: string;
      };
      height: {
        value: number;
        unit: string;
      };
      format?: unknown;
      orientation?: unknown;
    };
    atpages: unknown;
    initializeHandlers(): Handler[];
    registerHandlers(...handles: Handler[]): void;
    getParams(name: string): string | undefined;
    wrapContent(): DocumentFragment;
    removeStyles(doc?: Document): Array<string | Record<string, string>>;
    preview(
      content?: DocumentFragment | Node,
      stylesheets?: Array<string | Record<string, string>>,
      renderTo?: HTMLElement,
    ): Promise<FlowResult>;
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
    findEndToken(rendered: any, source: any): any;
    hasOverflow(element: any, bounds?: any): boolean;
    findOverflow(rendered: any, bounds?: any, gap?: number): Range;
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
    onOverflow(overflow: Overflow | undefined, rendered: Element, bounds: DOMRect): void;
  }

  // onBreakToken(breakToken, overflow, rendered);

  export class Handler {
    constructor(chunker: Chunker, polisher: Polisher, caller: any);
    afterPageLayout(pageElement: HTMLElement, page: any, breakToken: any): void;
  }

  export const registeredHandlers: Handler[];
  export function registerHandlers(...handles: Handler[]): void;
  export function initializeHandlers(chunker: Previewer, polisher: Polisher, caller: any): Handler[];
}
